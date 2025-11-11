package com.tirisano.mmogo.school.manager.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FirebaseService {

    private final Firestore firestore;

    public FirebaseService() {
        try {
            this.firestore = FirestoreClient.getFirestore();
            log.info("✅ Firestore client initialized successfully");
        } catch (Exception e) {
            log.error("❌ Failed to initialize Firestore client: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize Firestore client", e);
        }
    }

    public <T> CompletableFuture<String> save(String collection, T entity) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DocumentReference docRef = firestore.collection(collection).document();
                String generatedId = docRef.getId();

                // Try to set the ID on the entity using reflection
                setIdOnEntity(entity, collection, generatedId);

                ApiFuture<WriteResult> future = docRef.set(entity);
                WriteResult result = future.get(10, TimeUnit.SECONDS); // Add timeout

                log.debug("Document saved to collection '{}' with ID: {}", collection, generatedId);
                return generatedId;
            } catch (TimeoutException e) {
                log.error("Timeout saving document to collection: {}", collection, e);
                throw new RuntimeException("Timeout saving document to Firestore", e);
            } catch (Exception e) {
                log.error("Error saving document to collection: {}", collection, e);
                throw new RuntimeException("Error saving document to Firestore", e);
            }
        });
    }

    /**
     * Helper method to set the ID field on an entity based on the collection name
     */
    private <T> void setIdOnEntity(T entity, String collection, String id) {
        try {
            String idFieldName = getIdFieldName(collection);
            if (idFieldName != null) {
                java.lang.reflect.Method setter = findSetter(entity.getClass(), idFieldName);
                if (setter != null) {
                    setter.invoke(entity, id);
                    log.debug("Set {} to {} on entity", idFieldName, id);
                }
            }
        } catch (Exception e) {
            log.warn("Could not set ID field on entity for collection {}: {}", collection, e.getMessage());
        }
    }

    /**
     * Maps collection names to their corresponding ID field names
     */
    private String getIdFieldName(String collection) {
        return switch (collection) {
            case "parents" -> "parentId";
            case "students" -> "studentId";
            case "announcements" -> "announcementId";
            case "documentRequests" -> "requestId";
            case "trips" -> "tripId";
            case "meetings" -> "meetingId";
            case "payments" -> "paymentId";
            case "documents" -> "documentId";
            default -> null;
        };
    }

    /**
     * Finds the setter method for a given field name
     */
    private java.lang.reflect.Method findSetter(Class<?> clazz, String fieldName) {
        try {
            String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            return clazz.getMethod(setterName, String.class);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public <T> CompletableFuture<Void> save(String collection, T entity, String documentId) {
        return CompletableFuture.runAsync(() -> {
            try {
                DocumentReference docRef = firestore.collection(collection).document(documentId);
                ApiFuture<WriteResult> future = docRef.set(entity);
                WriteResult result = future.get(10, TimeUnit.SECONDS); // Add timeout

                log.debug("Document saved to collection '{}' with ID: {}", collection, documentId);
            } catch (TimeoutException e) {
                log.error("Timeout saving document with ID {} to collection: {}", documentId, collection, e);
                throw new RuntimeException("Timeout saving document to Firestore", e);
            } catch (Exception e) {
                log.error("Error saving document with ID {} to collection: {}", documentId, collection, e);
                throw new RuntimeException("Error saving document to Firestore", e);
            }
        });
    }

    public <T> CompletableFuture<T> findById(String collection, String id, Class<T> type) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DocumentReference docRef = firestore.collection(collection).document(id);
                ApiFuture<DocumentSnapshot> future = docRef.get();
                DocumentSnapshot document = future.get(10, TimeUnit.SECONDS); // Add timeout

                if (document.exists()) {
                    T result = document.toObject(type);
                    log.debug("Document found in collection '{}' with ID: {}", collection, id);
                    return result;
                } else {
                    log.debug("Document not found in collection '{}' with ID: {}", collection, id);
                    return null;
                }
            } catch (TimeoutException e) {
                log.error("Timeout finding document by ID {} in collection: {}", id, collection, e);
                throw new RuntimeException("Timeout finding document in Firestore", e);
            } catch (Exception e) {
                log.error("Error finding document by ID {} in collection: {}", id, collection, e);
                throw new RuntimeException("Error finding document in Firestore", e);
            }
        });
    }

    public <T> CompletableFuture<List<T>> findAll(String collection, Class<T> type) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                CollectionReference collectionRef = firestore.collection(collection);
                ApiFuture<QuerySnapshot> future = collectionRef.get();
                QuerySnapshot querySnapshot = future.get(10, TimeUnit.SECONDS); // Add timeout

                List<T> results = querySnapshot.getDocuments().stream()
                        .map(doc -> doc.toObject(type))
                        .collect(Collectors.toList());

                log.debug("Found {} documents in collection '{}'", results.size(), collection);
                return results;
            } catch (TimeoutException e) {
                log.error("Timeout finding all documents in collection: {}", collection, e);
                throw new RuntimeException("Timeout finding documents in Firestore", e);
            } catch (Exception e) {
                log.error("Error finding all documents in collection: {}", collection, e);
                throw new RuntimeException("Error finding documents in Firestore", e);
            }
        });
    }

    public <T> CompletableFuture<List<T>> findByField(String collection, String field, Object value, Class<T> type) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                CollectionReference collectionRef = firestore.collection(collection);
                Query query = collectionRef.whereEqualTo(field, value);
                ApiFuture<QuerySnapshot> future = query.get();
                QuerySnapshot querySnapshot = future.get(10, TimeUnit.SECONDS); // Add timeout

                List<T> results = querySnapshot.getDocuments().stream()
                        .map(doc -> doc.toObject(type))
                        .collect(Collectors.toList());

                log.debug("Found {} documents in collection '{}' where {} = {}",
                        results.size(), collection, field, value);
                return results;
            } catch (TimeoutException e) {
                log.error("Timeout querying documents in collection: {} with field: {} = {}",
                        collection, field, value, e);
                throw new RuntimeException("Timeout querying documents in Firestore", e);
            } catch (Exception e) {
                log.error("Error querying documents in collection: {} with field: {} = {}",
                        collection, field, value, e);
                throw new RuntimeException("Error querying documents in Firestore", e);
            }
        });
    }

    public CompletableFuture<Void> delete(String collection, String documentId) {
        return CompletableFuture.runAsync(() -> {
            try {
                DocumentReference docRef = firestore.collection(collection).document(documentId);
                ApiFuture<WriteResult> future = docRef.delete();
                WriteResult result = future.get(10, TimeUnit.SECONDS); // Add timeout

                log.debug("Document deleted from collection '{}' with ID: {}", collection, documentId);
            } catch (TimeoutException e) {
                log.error("Timeout deleting document with ID {} from collection: {}", documentId, collection, e);
                throw new RuntimeException("Timeout deleting document from Firestore", e);
            } catch (Exception e) {
                log.error("Error deleting document with ID {} from collection: {}", documentId, collection, e);
                throw new RuntimeException("Error deleting document from Firestore", e);
            }
        });
    }

    // Health check method
    public boolean isHealthy() {
        try {
            // Try a simple read operation to test connectivity
            CollectionReference testRef = firestore.collection("health_check");
            ApiFuture<QuerySnapshot> future = testRef.limit(1).get();
            future.get(5, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            log.warn("Firestore health check failed: {}", e.getMessage());
            return false;
        }
    }
}