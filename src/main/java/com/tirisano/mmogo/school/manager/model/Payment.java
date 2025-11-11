package com.tirisano.mmogo.school.manager.model;

import com.google.protobuf.DescriptorProtos;
import com.tirisano.mmogo.school.manager.enums.PaymentStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.google.cloud.Timestamp;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    private String paymentId;
    private String studentId;
    private String tripId;
    private String parentId;
    private BigDecimal amount;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;

    // Mock payment fields
    private String paymentMethod; // e.g., "Credit Card", "Debit Card", "Bank Transfer"
    private String transactionReference; // Mock transaction ID
    private String paymentNote; // Optional note from parent

    @Builder.Default
    private Timestamp createdAt = Timestamp.now();

    private Timestamp paidAt; // When payment was completed
}