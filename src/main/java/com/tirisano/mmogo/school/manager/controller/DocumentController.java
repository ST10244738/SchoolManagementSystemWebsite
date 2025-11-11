package com.tirisano.mmogo.school.manager.controller;

import com.tirisano.mmogo.school.manager.dto.ApiResponse;
import com.tirisano.mmogo.school.manager.enums.DocumentType;
import com.tirisano.mmogo.school.manager.model.Document;
import com.tirisano.mmogo.school.manager.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    // ==================== CRUD ENDPOINTS ====================

    // CREATE - Upload document
    @PostMapping
    public ResponseEntity<ApiResponse<Document>> uploadDocument(@Valid @RequestBody Document document) {
        try {
            Document savedDocument = documentService.uploadDocument(document);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(savedDocument, "Document uploaded successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // READ - Get all documents (Admin only)
    @GetMapping
    public ResponseEntity<ApiResponse<List<Document>>> getAllDocuments() {
        try {
            List<Document> documents = documentService.getAllDocuments();
            return ResponseEntity.ok(ApiResponse.success(documents));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // READ - Get document by ID
    @GetMapping("/{documentId}")
    public ResponseEntity<ApiResponse<Document>> getDocumentById(@PathVariable String documentId) {
        try {
            Document document = documentService.getDocumentById(documentId);
            if (document == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Document not found"));
            }
            return ResponseEntity.ok(ApiResponse.success(document));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // READ - Get documents by student ID
    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<Document>>> getDocumentsByStudentId(@PathVariable String studentId) {
        try {
            List<Document> documents = documentService.getDocumentsByStudentId(studentId);
            return ResponseEntity.ok(ApiResponse.success(documents));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // READ - Get documents by parent ID
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<ApiResponse<List<Document>>> getDocumentsByParentId(@PathVariable String parentId) {
        try {
            List<Document> documents = documentService.getDocumentsByParentId(parentId);
            return ResponseEntity.ok(ApiResponse.success(documents));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // READ - Get documents by type
    @GetMapping("/type/{documentType}")
    public ResponseEntity<ApiResponse<List<Document>>> getDocumentsByType(@PathVariable DocumentType documentType) {
        try {
            List<Document> documents = documentService.getDocumentsByType(documentType);
            return ResponseEntity.ok(ApiResponse.success(documents));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // READ - Get unverified documents (Admin only)
    @GetMapping("/unverified")
    public ResponseEntity<ApiResponse<List<Document>>> getUnverifiedDocuments() {
        try {
            List<Document> documents = documentService.getUnverifiedDocuments();
            return ResponseEntity.ok(ApiResponse.success(documents));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // UPDATE - Verify document (Admin only)
    @PutMapping("/{documentId}/verify")
    public ResponseEntity<ApiResponse<Document>> verifyDocument(
            @PathVariable String documentId,
            @RequestBody Map<String, String> body) {
        try {
            String verifiedBy = body.get("verifiedBy");
            if (verifiedBy == null || verifiedBy.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("verifiedBy field is required"));
            }
            Document document = documentService.verifyDocument(documentId, verifiedBy);
            return ResponseEntity.ok(ApiResponse.success(document, "Document verified successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // UPDATE - Update document
    @PutMapping("/{documentId}")
    public ResponseEntity<ApiResponse<Document>> updateDocument(
            @PathVariable String documentId,
            @Valid @RequestBody Document document) {
        try {
            Document updatedDocument = documentService.updateDocument(documentId, document);
            return ResponseEntity.ok(ApiResponse.success(updatedDocument, "Document updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // DELETE - Delete document
    @DeleteMapping("/{documentId}")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(@PathVariable String documentId) {
        try {
            documentService.deleteDocument(documentId);
            return ResponseEntity.ok(ApiResponse.success(null, "Document deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
