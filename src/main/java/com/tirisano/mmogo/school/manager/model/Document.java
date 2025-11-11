package com.tirisano.mmogo.school.manager.model;

import com.google.cloud.Timestamp;
import com.tirisano.mmogo.school.manager.enums.DocumentType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {
    private String documentId;

    @NotBlank
    private String fileName;

    @NotBlank
    private String fileUrl; // Firebase Storage URL or base64 encoded data

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    private String studentId; // Related student (for reports, certificates, etc.)
    private String parentId; // Who uploaded it
    private String uploadedBy; // User ID who uploaded
    private String uploadedByRole; // PARENT, ADMIN, etc.

    private String mimeType; // e.g., application/pdf, image/jpeg
    private Long fileSize; // in bytes

    private String description;

    @Builder.Default
    private Timestamp uploadedAt = Timestamp.now();

    @Builder.Default
    private boolean verified = false; // Admin can verify documents

    private String verifiedBy; // Admin who verified
    private Timestamp verifiedAt;
}
