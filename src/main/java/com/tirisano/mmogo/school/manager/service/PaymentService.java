package com.tirisano.mmogo.school.manager.service;

import com.google.cloud.Timestamp;
import com.tirisano.mmogo.school.manager.enums.PaymentStatus;
import com.tirisano.mmogo.school.manager.model.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final FirebaseService firebaseService;

    // CREATE - Mock payment
    public Payment createMockPayment(Payment payment) {
        try {
            // Generate mock transaction reference
            if (payment.getTransactionReference() == null || payment.getTransactionReference().isEmpty()) {
                payment.setTransactionReference("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            }

            // Set payment as completed for mock payment
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setPaidAt(Timestamp.now());

            firebaseService.save("payments", payment).join();
            log.info("Mock payment created successfully with ID: {}", payment.getPaymentId());
            return payment;
        } catch (Exception e) {
            log.error("Error creating mock payment", e);
            throw new RuntimeException("Failed to create payment: " + e.getMessage());
        }
    }

    // READ - Get all payments
    public List<Payment> getAllPayments() {
        try {
            return firebaseService.findAll("payments", Payment.class).join();
        } catch (Exception e) {
            log.error("Error fetching all payments", e);
            throw new RuntimeException("Failed to fetch payments: " + e.getMessage());
        }
    }

    // READ - Get payment by ID
    public Payment getPaymentById(String paymentId) {
        try {
            return firebaseService.findById("payments", paymentId, Payment.class).join();
        } catch (Exception e) {
            log.error("Error fetching payment by ID: {}", paymentId, e);
            throw new RuntimeException("Failed to fetch payment: " + e.getMessage());
        }
    }

    // READ - Get payments by student ID
    public List<Payment> getPaymentsByStudentId(String studentId) {
        try {
            return firebaseService.findByField("payments", "studentId", studentId, Payment.class).join();
        } catch (Exception e) {
            log.error("Error fetching payments for student: {}", studentId, e);
            throw new RuntimeException("Failed to fetch payments: " + e.getMessage());
        }
    }

    // READ - Get payments by parent ID
    public List<Payment> getPaymentsByParentId(String parentId) {
        try {
            return firebaseService.findByField("payments", "parentId", parentId, Payment.class).join();
        } catch (Exception e) {
            log.error("Error fetching payments for parent: {}", parentId, e);
            throw new RuntimeException("Failed to fetch payments: " + e.getMessage());
        }
    }

    // READ - Get payments by trip ID
    public List<Payment> getPaymentsByTripId(String tripId) {
        try {
            return firebaseService.findByField("payments", "tripId", tripId, Payment.class).join();
        } catch (Exception e) {
            log.error("Error fetching payments for trip: {}", tripId, e);
            throw new RuntimeException("Failed to fetch payments: " + e.getMessage());
        }
    }

    // READ - Get payments by status
    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        try {
            return firebaseService.findByField("payments", "status", status, Payment.class).join();
        } catch (Exception e) {
            log.error("Error fetching payments by status: {}", status, e);
            throw new RuntimeException("Failed to fetch payments: " + e.getMessage());
        }
    }

    // UPDATE - Update payment status
    public Payment updatePaymentStatus(String paymentId, PaymentStatus newStatus) {
        try {
            Payment payment = getPaymentById(paymentId);
            if (payment == null) {
                throw new RuntimeException("Payment not found with ID: " + paymentId);
            }

            payment.setStatus(newStatus);
            if (newStatus == PaymentStatus.COMPLETED && payment.getPaidAt() == null) {
                payment.setPaidAt(Timestamp.now());
            }

            firebaseService.save("payments", payment, paymentId).join();
            log.info("Payment status updated successfully: {}", paymentId);
            return payment;
        } catch (Exception e) {
            log.error("Error updating payment status: {}", paymentId, e);
            throw new RuntimeException("Failed to update payment: " + e.getMessage());
        }
    }

    // UPDATE - Update payment
    public Payment updatePayment(String paymentId, Payment updatedPayment) {
        try {
            Payment existingPayment = getPaymentById(paymentId);
            if (existingPayment == null) {
                throw new RuntimeException("Payment not found with ID: " + paymentId);
            }

            updatedPayment.setPaymentId(paymentId);
            // Preserve original creation timestamp
            if (updatedPayment.getCreatedAt() == null) {
                updatedPayment.setCreatedAt(existingPayment.getCreatedAt());
            }

            firebaseService.save("payments", updatedPayment, paymentId).join();
            log.info("Payment updated successfully: {}", paymentId);
            return updatedPayment;
        } catch (Exception e) {
            log.error("Error updating payment: {}", paymentId, e);
            throw new RuntimeException("Failed to update payment: " + e.getMessage());
        }
    }

    // DELETE - Delete payment
    public void deletePayment(String paymentId) {
        try {
            Payment payment = getPaymentById(paymentId);
            if (payment == null) {
                throw new RuntimeException("Payment not found with ID: " + paymentId);
            }

            firebaseService.delete("payments", paymentId).join();
            log.info("Payment deleted successfully: {}", paymentId);
        } catch (Exception e) {
            log.error("Error deleting payment: {}", paymentId, e);
            throw new RuntimeException("Failed to delete payment: " + e.getMessage());
        }
    }

    // Check if student has paid for a trip
    public boolean hasStudentPaidForTrip(String studentId, String tripId) {
        try {
            List<Payment> payments = getPaymentsByStudentId(studentId);
            return payments.stream()
                    .anyMatch(p -> p.getTripId().equals(tripId) && p.getStatus() == PaymentStatus.COMPLETED);
        } catch (Exception e) {
            log.error("Error checking payment status for student {} and trip {}", studentId, tripId, e);
            return false;
        }
    }
}
