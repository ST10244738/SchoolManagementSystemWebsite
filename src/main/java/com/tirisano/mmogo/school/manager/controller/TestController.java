package com.tirisano.mmogo.school.manager.controller;

import com.tirisano.mmogo.school.manager.dto.ApiResponse;
import com.tirisano.mmogo.school.manager.service.FirebaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.google.cloud.Timestamp;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    private final FirebaseService firebaseService;

    public TestController(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    // Simple health check - just visit in browser
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", Timestamp.now().toString());
        response.put("message", "Backend is running!");
        response.put("firebase", firebaseService.isHealthy() ? "CONNECTED" : "DISCONNECTED");
        return ResponseEntity.ok(response);
    }

    // Test Firebase write operation
    @GetMapping("/firebase")
    public ResponseEntity<ApiResponse<Map<String, String>>> testFirebaseConnection() {
        try {
            log.info("Testing Firebase connection...");

            // Test writing to Firestore
            Map<String, Object> testData = new HashMap<>();
            testData.put("message", "Hello Firebase!");
            testData.put("timestamp", Timestamp.now().toString());
            testData.put("status", "connected");
            testData.put("testNumber", Math.random());

            String docId = firebaseService.save("test_collection", testData).join();

            Map<String, String> result = new HashMap<>();
            result.put("documentId", docId);
            result.put("message", "Firebase connected successfully!");
            result.put("collection", "test_collection");

            log.info("✅ Firebase test successful! Document ID: {}", docId);
            return ResponseEntity.ok(ApiResponse.success(result, "Firebase working!"));

        } catch (Exception e) {
            log.error("❌ Firebase test failed", e);
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Firebase connection failed: " + e.getMessage())
            );
        }
    }

    // Test reading from Firebase
    @GetMapping("/firebase/read")
    public ResponseEntity<ApiResponse<Object>> testFirebaseRead() {
        try {
            log.info("Testing Firebase read operation...");

            var documents = firebaseService.findAll("test_collection", Map.class).join();

            return ResponseEntity.ok(ApiResponse.success(
                    documents,
                    "Found " + documents.size() + " test documents"
            ));

        } catch (Exception e) {
            log.error("❌ Firebase read failed", e);
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Firebase read failed: " + e.getMessage())
            );
        }
    }
}