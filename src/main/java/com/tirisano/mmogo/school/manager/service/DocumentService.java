package com.tirisano.mmogo.school.manager.service;

import com.google.cloud.Timestamp;
import com.tirisano.mmogo.school.manager.enums.DocumentType;
import com.tirisano.mmogo.school.manager.model.Document;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final FirebaseService firebaseService;

    // CREATE - Upload document
    public Document uploadDocument(Document document) {
        try {
            if (document.getUploadedAt() == null) {
                document.setUploadedAt(Timestamp.now());
            }
            firebaseService.save("documents", document).join();
            log.info("Document uploaded successfully with ID: {}", document.getDocumentId());
            return document;
        } catch (Exception e) {
            log.error("Error uploading document", e);
            throw new RuntimeException("Failed to upload document: " + e.getMessage());
        }
    }

    // READ - Get all documents
    public List<Document> getAllDocuments() {
        try {
            return firebaseService.findAll("documents", Document.class).join();
        } catch (Exception e) {
            log.error("Error fetching all documents", e);
            throw new RuntimeException("Failed to fetch documents: " + e.getMessage());
        }
    }

    // READ - Get document by ID
    public Document getDocumentById(String documentId) {
        try {
            return firebaseService.findById("documents", documentId, Document.class).join();
        } catch (Exception e) {
            log.error("Error fetching document by ID: {}", documentId, e);
            throw new RuntimeException("Failed to fetch document: " + e.getMessage());
        }
    }

    // READ - Get documents by student ID
    public List<Document> getDocumentsByStudentId(String studentId) {
        try {
            return firebaseService.findByField("documents", "studentId", studentId, Document.class).join();
        } catch (Exception e) {
            log.error("Error fetching documents for student: {}", studentId, e);
            throw new RuntimeException("Failed to fetch documents: " + e.getMessage());
        }
    }

    // READ - Get documents by parent ID
    public List<Document> getDocumentsByParentId(String parentId) {
        try {
            return firebaseService.findByField("documents", "parentId", parentId, Document.class).join();
        } catch (Exception e) {
            log.error("Error fetching documents for parent: {}", parentId, e);
            throw new RuntimeException("Failed to fetch documents: " + e.getMessage());
        }
    }

    // READ - Get documents by type
    public List<Document> getDocumentsByType(DocumentType documentType) {
        try {
            return firebaseService.findByField("documents", "documentType", documentType, Document.class).join();
        } catch (Exception e) {
            log.error("Error fetching documents by type: {}", documentType, e);
            throw new RuntimeException("Failed to fetch documents: " + e.getMessage());
        }
    }

    // READ - Get unverified documents (for admin review)
    public List<Document> getUnverifiedDocuments() {
        try {
            return firebaseService.findByField("documents", "verified", false, Document.class).join();
        } catch (Exception e) {
            log.error("Error fetching unverified documents", e);
            throw new RuntimeException("Failed to fetch documents: " + e.getMessage());
        }
    }

    // UPDATE - Verify document (admin only)
    public Document verifyDocument(String documentId, String verifiedBy) {
        try {
            Document document = getDocumentById(documentId);
            if (document == null) {
                throw new RuntimeException("Document not found with ID: " + documentId);
            }

            document.setVerified(true);
            document.setVerifiedBy(verifiedBy);
            document.setVerifiedAt(Timestamp.now());

            firebaseService.save("documents", document, documentId).join();
            log.info("Document verified successfully: {}", documentId);
            return document;
        } catch (Exception e) {
            log.error("Error verifying document: {}", documentId, e);
            throw new RuntimeException("Failed to verify document: " + e.getMessage());
        }
    }

    // UPDATE - Update document
    public Document updateDocument(String documentId, Document updatedDocument) {
        try {
            Document existingDocument = getDocumentById(documentId);
            if (existingDocument == null) {
                throw new RuntimeException("Document not found with ID: " + documentId);
            }

            updatedDocument.setDocumentId(documentId);
            // Preserve original upload timestamp
            if (updatedDocument.getUploadedAt() == null) {
                updatedDocument.setUploadedAt(existingDocument.getUploadedAt());
            }

            firebaseService.save("documents", updatedDocument, documentId).join();
            log.info("Document updated successfully: {}", documentId);
            return updatedDocument;
        } catch (Exception e) {
            log.error("Error updating document: {}", documentId, e);
            throw new RuntimeException("Failed to update document: " + e.getMessage());
        }
    }

    // DELETE - Delete document
    public void deleteDocument(String documentId) {
        try {
            Document document = getDocumentById(documentId);
            if (document == null) {
                throw new RuntimeException("Document not found with ID: " + documentId);
            }

            firebaseService.delete("documents", documentId).join();
            log.info("Document deleted successfully: {}", documentId);
        } catch (Exception e) {
            log.error("Error deleting document: {}", documentId, e);
            throw new RuntimeException("Failed to delete document: " + e.getMessage());
        }
    }
}
