package com.tirisano.mmogo.school.manager.controller;

import com.tirisano.mmogo.school.manager.dto.ApiResponse;
import com.tirisano.mmogo.school.manager.dto.LoginRequest;
import com.tirisano.mmogo.school.manager.dto.RegisterRequest;
import com.tirisano.mmogo.school.manager.dto.UserDto;
import com.tirisano.mmogo.school.manager.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

@Validated
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            UserDto user = authService.registerUser(request);
            return ResponseEntity.ok(ApiResponse.success(user, "Registration successful"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDto>> login(@Valid @RequestBody LoginRequest request) {
        try {
            UserDto user = authService.authenticateUser(request);
            return ResponseEntity.ok(ApiResponse.success(user, "Login successful"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody java.util.Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Email is required"));
            }

            authService.sendPasswordResetEmail(email);
            return ResponseEntity.ok(ApiResponse.success(
                    "Password reset email sent",
                    "If an account exists with this email, you will receive a password reset link"
            ));
        } catch (Exception e) {
            // For security, we return a generic message even if user doesn't exist
            return ResponseEntity.ok(ApiResponse.success(
                    "Password reset email sent",
                    "If an account exists with this email, you will receive a password reset link"
            ));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody java.util.Map<String, String> request) {
        try {
            String uid = request.get("uid");
            String newPassword = request.get("newPassword");

            if (uid == null || uid.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("User ID is required"));
            }
            if (newPassword == null || newPassword.length() < 6) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Password must be at least 6 characters"));
            }

            authService.updatePassword(uid, newPassword);
            return ResponseEntity.ok(ApiResponse.success(
                    "Password updated successfully",
                    "You can now login with your new password"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to reset password: " + e.getMessage()));
        }
    }

    @GetMapping("/user-by-email")
    public ResponseEntity<ApiResponse<UserDto>> getUserByEmail(@RequestParam String email) {
        try {
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Email is required"));
            }

            var user = authService.getUserByEmail(email);
            UserDto userDto = UserDto.builder()
                    .uid(user.getUid())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .role(user.getRole())
                    .build();

            return ResponseEntity.ok(ApiResponse.success(userDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}