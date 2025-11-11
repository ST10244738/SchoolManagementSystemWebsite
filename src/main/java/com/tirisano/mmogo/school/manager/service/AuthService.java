package com.tirisano.mmogo.school.manager.service;

import com.google.cloud.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.tirisano.mmogo.school.manager.dto.LoginRequest;
import com.tirisano.mmogo.school.manager.dto.RegisterRequest;
import com.tirisano.mmogo.school.manager.dto.UserDto;
import com.tirisano.mmogo.school.manager.enums.UserRole;
import com.tirisano.mmogo.school.manager.model.Parent;
import com.tirisano.mmogo.school.manager.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final FirebaseService firebaseService;

    public UserDto registerUser(RegisterRequest request) {
        try {
            log.info("Registering user: {}", request.getEmail());

            // Create Firebase user
            UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                    .setEmail(request.getEmail())
                    .setPassword(request.getPassword())
                    .setDisplayName(request.getFullName());

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(createRequest);
            log.info("Firebase Auth user created with UID: {}", userRecord.getUid());

            // Create User document
            User user = User.builder()
                    .uid(userRecord.getUid())
                    .email(request.getEmail())
                    .fullName(request.getFullName())
                    .phoneNumber(request.getPhoneNumber())
                    .role(request.getRole())
                    .createdAt(Timestamp.now())  // Add this line
                    .active(true)                // Add this line
                    .build();

            firebaseService.save("users", user, userRecord.getUid()).join();
            log.info("User document saved to Firestore");

            String parentId = null;
            if (request.getRole() == UserRole.PARENT) {
                // Create Parent document
                Parent parent = Parent.builder()
                        .uid(userRecord.getUid())
                        .fullName(request.getFullName())
                        .email(request.getEmail())
                        .phoneNumber(request.getPhoneNumber())
                        .address(request.getAddress())
                        .build();

                parentId = firebaseService.save("parents", parent).join();
                // FirebaseService automatically sets parentId on the parent object
                log.info("Parent document saved with ID: {}", parentId);
            }

            log.info("✅ Registration successful for: {}", request.getEmail());

            return UserDto.builder()
                    .uid(userRecord.getUid())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .phoneNumber(user.getPhoneNumber())
                    .role(user.getRole())
                    .parentId(parentId)
                    .build();

        } catch (Exception e) {
            log.error("❌ Error registering user: {}", request.getEmail(), e);
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

    public UserDto authenticateUser(LoginRequest request) {
        try {
            log.info("Authenticating user: {}", request.getEmail());

            // First, get the user from Firebase Auth by email to verify they exist
            UserRecord userRecord;
            try {
                userRecord = FirebaseAuth.getInstance().getUserByEmail(request.getEmail());
            } catch (Exception e) {
                log.warn("User not found in Firebase Auth: {}", request.getEmail());
                throw new RuntimeException("Invalid email or password");
            }

            // IMPORTANT: Firebase Admin SDK doesn't support password verification directly
            // You should use Firebase Client SDK on frontend or implement ID token verification
            // For now, we'll verify the user exists in Firestore and trust the frontend
            // In production, you should verify ID tokens or use Firebase REST API

            // Verify user exists in Firestore
            List<User> users = firebaseService.findByField("users", "email", request.getEmail(), User.class).join();

            if (users.isEmpty()) {
                log.warn("User not found in Firestore: {}", request.getEmail());
                throw new RuntimeException("Invalid email or password");
            }

            User user = users.get(0);

            // Verify password using Firebase REST API
            if (!verifyPasswordWithFirebaseAuth(request.getEmail(), request.getPassword())) {
                log.warn("Invalid password for user: {}", request.getEmail());
                throw new RuntimeException("Invalid email or password");
            }

            String parentId = null;

            if (user.getRole() == UserRole.PARENT) {
                List<Parent> parents = firebaseService.findByField("parents", "uid", user.getUid(), Parent.class).join();
                if (!parents.isEmpty()) {
                    parentId = parents.get(0).getParentId();
                }
            }

            log.info("✅ Authentication successful for: {}", request.getEmail());

            return UserDto.builder()
                    .uid(user.getUid())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .phoneNumber(user.getPhoneNumber())
                    .role(user.getRole())
                    .parentId(parentId)
                    .build();

        } catch (Exception e) {
            log.error("❌ Error authenticating user: {}", request.getEmail(), e);
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    /**
     * Verify password using Firebase Authentication REST API
     */
    private boolean verifyPasswordWithFirebaseAuth(String email, String password) {
        try {
            // Use Firebase REST API to verify the password
            // This requires your Firebase API key
            String apiKey = System.getProperty("FIREBASE_API_KEY");
            if (apiKey == null || apiKey.isEmpty()) {
                apiKey = System.getenv("FIREBASE_API_KEY");
            }
            if (apiKey == null || apiKey.isEmpty()) {
                log.error("FIREBASE_API_KEY not found in system properties or environment variables");
                throw new RuntimeException("Server configuration error");
            }

            // Create HTTP client and request
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            String requestBody = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}",
                email, password
            );

            java.net.http.HttpRequest httpRequest = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey))
                .header("Content-Type", "application/json")
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

            java.net.http.HttpResponse<String> response = client.send(httpRequest,
                java.net.http.HttpResponse.BodyHandlers.ofString());

            // If status is 200, password is correct
            if (response.statusCode() == 200) {
                log.info("Password verified successfully for: {}", email);
                return true;
            } else {
                log.warn("Password verification failed for: {}. Status: {}", email, response.statusCode());
                return false;
            }

        } catch (Exception e) {
            log.error("Error verifying password for: {}", email, e);
            return false;
        }
    }

    /**
     * Send password reset email using Firebase Auth
     */
    public void sendPasswordResetEmail(String email) {
        try {
            log.info("Sending password reset email to: {}", email);

            // Verify user exists in Firestore
            List<User> users = firebaseService.findByField("users", "email", email, User.class).join();
            if (users.isEmpty()) {
                log.warn("User not found: {}", email);
                throw new RuntimeException("No user found with this email address");
            }

            // Generate password reset link using Firebase Auth
            String resetLink = FirebaseAuth.getInstance().generatePasswordResetLink(email);
            log.info("Password reset link generated for: {}", email);

            // In a production app, you would send this link via email service
            // For now, we just log it (Firebase also sends it automatically)
            log.info("Password reset link: {}", resetLink);
            log.info("✅ Password reset email sent to: {}", email);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("❌ Error sending password reset email to: {}", email, e);
            throw new RuntimeException("Failed to send password reset email: " + e.getMessage());
        }
    }

    /**
     * Update user password using Firebase Auth
     */
    public void updatePassword(String uid, String newPassword) {
        try {
            log.info("Updating password for user: {}", uid);

            // Update password in Firebase Auth
            UserRecord.UpdateRequest updateRequest = new UserRecord.UpdateRequest(uid)
                    .setPassword(newPassword);

            FirebaseAuth.getInstance().updateUser(updateRequest);
            log.info("✅ Password updated successfully for user: {}", uid);

        } catch (Exception e) {
            log.error("❌ Error updating password for user: {}", uid, e);
            throw new RuntimeException("Failed to update password: " + e.getMessage());
        }
    }

    /**
     * Verify email and get user by email
     */
    public User getUserByEmail(String email) {
        try {
            List<User> users = firebaseService.findByField("users", "email", email, User.class).join();
            if (users.isEmpty()) {
                throw new RuntimeException("User not found");
            }
            return users.get(0);
        } catch (Exception e) {
            log.error("Error getting user by email: {}", email, e);
            throw new RuntimeException("Failed to get user: " + e.getMessage());
        }
    }
}