package com.tirisano.mmogo.school.manager.service;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.tirisano.mmogo.school.manager.enums.PaymentStatus;
import com.tirisano.mmogo.school.manager.model.Payment;
import com.tirisano.mmogo.school.manager.model.Student;
import com.tirisano.mmogo.school.manager.model.Trip;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripService {

    private final FirebaseService firebaseService;

    // CREATE
    public Trip createTrip(Trip trip) {
        try {
            firebaseService.save("trips", trip).join();
            log.info("Trip created successfully with ID: {}", trip.getTripId());
            return trip;
        } catch (Exception e) {
            log.error("Error creating trip", e);
            throw new RuntimeException("Failed to create trip: " + e.getMessage());
        }
    }

    // READ - Get all trips
    public List<Trip> findAll() {
        try {
            return firebaseService.findAll("trips", Trip.class).join();
        } catch (Exception e) {
            log.error("Error fetching all trips", e);
            throw new RuntimeException("Failed to fetch trips: " + e.getMessage());
        }
    }

    // READ - Get trip by ID
    public Trip findById(String tripId) {
        try {
            return firebaseService.findById("trips", tripId, Trip.class).join();
        } catch (Exception e) {
            log.error("Error fetching trip by ID: {}", tripId, e);
            throw new RuntimeException("Failed to fetch trip: " + e.getMessage());
        }
    }

    // UPDATE
    public Trip updateTrip(String tripId, Trip trip) {
        try {
            Trip existingTrip = findById(tripId);
            if (existingTrip == null) {
                throw new RuntimeException("Trip not found with ID: " + tripId);
            }

            trip.setTripId(tripId);
            // Preserve original creation timestamp
            if (trip.getCreatedAt() == null) {
                trip.setCreatedAt(existingTrip.getCreatedAt());
            }
            // Preserve registered students list if not provided
            if (trip.getRegisteredStudents() == null) {
                trip.setRegisteredStudents(existingTrip.getRegisteredStudents());
            }

            firebaseService.save("trips", trip, tripId).join();
            log.info("Trip updated successfully: {}", tripId);
            return trip;
        } catch (Exception e) {
            log.error("Error updating trip: {}", tripId, e);
            throw new RuntimeException("Failed to update trip: " + e.getMessage());
        }
    }

    // DELETE
    public void deleteTrip(String tripId) {
        try {
            Trip trip = findById(tripId);
            if (trip == null) {
                throw new RuntimeException("Trip not found with ID: " + tripId);
            }

            firebaseService.delete("trips", tripId).join();
            log.info("Trip deleted successfully: {}", tripId);
        } catch (Exception e) {
            log.error("Error deleting trip: {}", tripId, e);
            throw new RuntimeException("Failed to delete trip: " + e.getMessage());
        }
    }

    // Register student for trip (with mock payment)
    public void registerStudent(String tripId, String studentId, String parentId, String paymentMethod) {
        try {
            Trip trip = findById(tripId);
            if (trip == null) {
                throw new RuntimeException("Trip not found with ID: " + tripId);
            }

            if (trip.getRegisteredStudents().contains(studentId)) {
                throw new RuntimeException("Student already registered for this trip");
            }

            // Add student to trip
            trip.getRegisteredStudents().add(studentId);
            firebaseService.save("trips", trip, tripId).join();

            // Create mock payment record
            Payment payment = Payment.builder()
                    .studentId(studentId)
                    .tripId(tripId)
                    .parentId(parentId)
                    .amount(trip.getPrice())
                    .status(PaymentStatus.COMPLETED)
                    .paymentMethod(paymentMethod != null ? paymentMethod : "Credit Card")
                    .transactionReference("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                    .paidAt(Timestamp.now())
                    .build();

            firebaseService.save("payments", payment).join();
            log.info("Student {} registered for trip {} with mock payment", studentId, tripId);
        } catch (Exception e) {
            log.error("Error registering student {} for trip {}", studentId, tripId, e);
            throw new RuntimeException("Failed to register student for trip: " + e.getMessage());
        }
    }

    // Unregister student from trip
    public void unregisterStudent(String tripId, String studentId) {
        try {
            Trip trip = findById(tripId);
            if (trip == null) {
                throw new RuntimeException("Trip not found with ID: " + tripId);
            }

            trip.getRegisteredStudents().remove(studentId);
            firebaseService.save("trips", trip, tripId).join();
            log.info("Student {} unregistered from trip {}", studentId, tripId);
        } catch (Exception e) {
            log.error("Error unregistering student {} from trip {}", studentId, tripId, e);
            throw new RuntimeException("Failed to unregister student from trip: " + e.getMessage());
        }
    }

    // Put trip on hold (set active to false)
    public Trip holdTrip(String tripId) {
        try {
            Trip trip = findById(tripId);
            if (trip == null) {
                throw new RuntimeException("Trip not found with ID: " + tripId);
            }
            trip.setActive(false);
            firebaseService.save("trips", trip, tripId).join();
            log.info("Trip {} put on hold", tripId);
            return trip;
        } catch (Exception e) {
            log.error("Error putting trip {} on hold", tripId, e);
            throw new RuntimeException("Failed to put trip on hold: " + e.getMessage());
        }
    }

    // Activate trip (set active to true)
    public Trip activateTrip(String tripId) {
        try {
            Trip trip = findById(tripId);
            if (trip == null) {
                throw new RuntimeException("Trip not found with ID: " + tripId);
            }
            trip.setActive(true);
            firebaseService.save("trips", trip, tripId).join();
            log.info("Trip {} activated", tripId);
            return trip;
        } catch (Exception e) {
            log.error("Error activating trip {}", tripId, e);
            throw new RuntimeException("Failed to activate trip: " + e.getMessage());
        }
    }

    // Upload/Update trip image
    public Trip updateTripImage(String tripId, String imageData) {
        try {
            Trip trip = findById(tripId);
            if (trip == null) {
                throw new RuntimeException("Trip not found with ID: " + tripId);
            }
            trip.setImageUrl(imageData);
            firebaseService.save("trips", trip, tripId).join();
            log.info("Trip {} image updated", tripId);
            return trip;
        } catch (Exception e) {
            log.error("Error updating image for trip {}", tripId, e);
            throw new RuntimeException("Failed to update trip image: " + e.getMessage());
        }
    }

    // Get paid students for a trip, grouped by grade
    public Map<String, List<Student>> getPaidStudentsByGrade(String tripId) {
        try {
            Trip trip = findById(tripId);
            if (trip == null) {
                throw new RuntimeException("Trip not found with ID: " + tripId);
            }

            List<String> registeredStudentIds = trip.getRegisteredStudents();
            if (registeredStudentIds == null || registeredStudentIds.isEmpty()) {
                return new HashMap<>();
            }

            // Fetch all students
            List<Student> allStudents = firebaseService.findAll("students", Student.class).join();

            // Filter students who are registered for this trip
            List<Student> paidStudents = allStudents.stream()
                    .filter(student -> registeredStudentIds.contains(student.getStudentId()))
                    .collect(Collectors.toList());

            // Group by grade
            Map<String, List<Student>> studentsByGrade = paidStudents.stream()
                    .collect(Collectors.groupingBy(
                            student -> student.getGrade() != null ? student.getGrade() : "Unknown",
                            Collectors.toList()
                    ));

            log.info("Retrieved {} paid students for trip {}, grouped by {} grades",
                    paidStudents.size(), tripId, studentsByGrade.size());
            return studentsByGrade;
        } catch (Exception e) {
            log.error("Error getting paid students for trip {}", tripId, e);
            throw new RuntimeException("Failed to get paid students: " + e.getMessage());
        }
    }
}
