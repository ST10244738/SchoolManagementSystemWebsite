package com.tirisano.mmogo.school.manager.controller;

import com.tirisano.mmogo.school.manager.dto.ApiResponse;
import com.tirisano.mmogo.school.manager.enums.PaymentStatus;
import com.tirisano.mmogo.school.manager.model.Payment;
import com.tirisano.mmogo.school.manager.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // ==================== CRUD ENDPOINTS ====================

    // CREATE - Mock payment (for parents)
    @PostMapping("/mock")
    public ResponseEntity<ApiResponse<Payment>> createMockPayment(@Valid @RequestBody Payment payment) {
        try {
            Payment savedPayment = paymentService.createMockPayment(payment);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(savedPayment, "Payment processed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // READ - Get all payments (Admin only)
    @GetMapping
    public ResponseEntity<ApiResponse<List<Payment>>> getAllPayments() {
        try {
            List<Payment> payments = paymentService.getAllPayments();
            return ResponseEntity.ok(ApiResponse.success(payments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // READ - Get payment by ID
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<Payment>> getPaymentById(@PathVariable String paymentId) {
        try {
            Payment payment = paymentService.getPaymentById(paymentId);
            if (payment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Payment not found"));
            }
            return ResponseEntity.ok(ApiResponse.success(payment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // READ - Get payments by student ID
    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<Payment>>> getPaymentsByStudentId(@PathVariable String studentId) {
        try {
            List<Payment> payments = paymentService.getPaymentsByStudentId(studentId);
            return ResponseEntity.ok(ApiResponse.success(payments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // READ - Get payments by parent ID
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<ApiResponse<List<Payment>>> getPaymentsByParentId(@PathVariable String parentId) {
        try {
            List<Payment> payments = paymentService.getPaymentsByParentId(parentId);
            return ResponseEntity.ok(ApiResponse.success(payments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // READ - Get payments by trip ID
    @GetMapping("/trip/{tripId}")
    public ResponseEntity<ApiResponse<List<Payment>>> getPaymentsByTripId(@PathVariable String tripId) {
        try {
            List<Payment> payments = paymentService.getPaymentsByTripId(tripId);
            return ResponseEntity.ok(ApiResponse.success(payments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // READ - Get payments by status
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Payment>>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        try {
            List<Payment> payments = paymentService.getPaymentsByStatus(status);
            return ResponseEntity.ok(ApiResponse.success(payments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // READ - Check if student has paid for trip
    @GetMapping("/check/{studentId}/{tripId}")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkPaymentStatus(
            @PathVariable String studentId,
            @PathVariable String tripId) {
        try {
            boolean hasPaid = paymentService.hasStudentPaidForTrip(studentId, tripId);
            return ResponseEntity.ok(ApiResponse.success(Map.of("hasPaid", hasPaid)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // UPDATE - Update payment status
    @PutMapping("/{paymentId}/status")
    public ResponseEntity<ApiResponse<Payment>> updatePaymentStatus(
            @PathVariable String paymentId,
            @RequestBody Map<String, String> body) {
        try {
            String statusStr = body.get("status");
            if (statusStr == null || statusStr.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("status field is required"));
            }
            PaymentStatus status = PaymentStatus.valueOf(statusStr);
            Payment payment = paymentService.updatePaymentStatus(paymentId, status);
            return ResponseEntity.ok(ApiResponse.success(payment, "Payment status updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid payment status"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // UPDATE - Update payment
    @PutMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<Payment>> updatePayment(
            @PathVariable String paymentId,
            @Valid @RequestBody Payment payment) {
        try {
            Payment updatedPayment = paymentService.updatePayment(paymentId, payment);
            return ResponseEntity.ok(ApiResponse.success(updatedPayment, "Payment updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // DELETE - Delete payment
    @DeleteMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<Void>> deletePayment(@PathVariable String paymentId) {
        try {
            paymentService.deletePayment(paymentId);
            return ResponseEntity.ok(ApiResponse.success(null, "Payment deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
