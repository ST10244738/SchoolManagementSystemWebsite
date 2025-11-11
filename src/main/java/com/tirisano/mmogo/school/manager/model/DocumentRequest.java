package com.tirisano.mmogo.school.manager.model;

import com.tirisano.mmogo.school.manager.enums.RequestStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.w3c.dom.DocumentType;

import com.google.cloud.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentRequest {
    private String requestId;
    private String parentId;
    private String studentId;
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;
    private String reason;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;
    @Builder.Default
    private Timestamp createdAt = Timestamp.now();
}
