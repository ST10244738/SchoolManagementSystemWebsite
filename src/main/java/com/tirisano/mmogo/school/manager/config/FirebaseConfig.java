package com.tirisano.mmogo.school.manager.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import java.io.InputStream;

@Configuration
@Slf4j
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            // Load .env file if it exists
            try {
                Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
                String apiKey = dotenv.get("FIREBASE_API_KEY");
                if (apiKey != null && !apiKey.isEmpty()) {
                    System.setProperty("FIREBASE_API_KEY", apiKey);
                    log.info("✅ Firebase API key loaded from .env file");
                }
            } catch (Exception e) {
                log.warn("Could not load .env file, using system environment variables");
            }

            // Check if Firebase is already initialized
            if (!FirebaseApp.getApps().isEmpty()) {
                log.info("Firebase already initialized");
                return;
            }

            // Load service account key
            InputStream serviceAccount = getClass().getClassLoader()
                    .getResourceAsStream("firebase-service-account.json");

            if (serviceAccount == null) {
                throw new RuntimeException("Firebase service account file not found in resources folder");
            }

            // Create Firebase options
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // Initialize Firebase
            FirebaseApp.initializeApp(options);

            log.info("✅ Firebase initialized successfully!");

        } catch (Exception e) {
            log.error("❌ Failed to initialize Firebase: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
    }
}
