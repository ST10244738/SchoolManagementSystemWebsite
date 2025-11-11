package com.tirisano.mmogo.school.manager.controller;


import com.tirisano.mmogo.school.manager.dto.ApiResponse;
import com.tirisano.mmogo.school.manager.model.Student;
import com.tirisano.mmogo.school.manager.model.Trip;
import com.tirisano.mmogo.school.manager.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    // ==================== CRUD ENDPOINTS ====================

    // CREATE - Create trip (Admin only)
    @PostMapping
    public ResponseEntity<ApiResponse<Trip>> createTrip(@Valid @RequestBody Trip trip) {
        try {
            Trip savedTrip = tripService.createTrip(trip);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(savedTrip, "Trip created successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // READ - Get all trips
    @GetMapping
    public ResponseEntity<ApiResponse<List<Trip>>> getAllTrips() {
        try {
            List<Trip> trips = tripService.findAll();
            return ResponseEntity.ok(ApiResponse.success(trips));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // READ - Get trip by ID
    @GetMapping("/{tripId}")
    public ResponseEntity<ApiResponse<Trip>> getTrip(@PathVariable String tripId) {
        try {
            Trip trip = tripService.findById(tripId);
            if (trip == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Trip not found"));
            }
            return ResponseEntity.ok(ApiResponse.success(trip));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // UPDATE - Update trip (Admin only)
    @PutMapping("/{tripId}")
    public ResponseEntity<ApiResponse<Trip>> updateTrip(
            @PathVariable String tripId,
            @Valid @RequestBody Trip trip) {
        try {
            Trip updatedTrip = tripService.updateTrip(tripId, trip);
            return ResponseEntity.ok(ApiResponse.success(updatedTrip, "Trip updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // DELETE - Delete trip (Admin only)
    @DeleteMapping("/{tripId}")
    public ResponseEntity<ApiResponse<Void>> deleteTrip(@PathVariable String tripId) {
        try {
            tripService.deleteTrip(tripId);
            return ResponseEntity.ok(ApiResponse.success(null, "Trip deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ==================== REGISTRATION ENDPOINTS ====================

    // Register student for trip (with mock payment)
    @PostMapping("/{tripId}/register")
    public ResponseEntity<ApiResponse<String>> registerForTrip(
            @PathVariable String tripId,
            @RequestBody Map<String, String> body) {
        try {
            String studentId = body.get("studentId");
            String parentId = body.get("parentId");
            String paymentMethod = body.get("paymentMethod");

            if (studentId == null || studentId.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("studentId is required"));
            }
            if (parentId == null || parentId.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("parentId is required"));
            }

            tripService.registerStudent(tripId, studentId, parentId, paymentMethod);
            return ResponseEntity.ok(ApiResponse.success("Student registered and payment processed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // Unregister student from trip
    @DeleteMapping("/{tripId}/register/{studentId}")
    public ResponseEntity<ApiResponse<String>> unregisterFromTrip(
            @PathVariable String tripId,
            @PathVariable String studentId) {
        try {
            tripService.unregisterStudent(tripId, studentId);
            return ResponseEntity.ok(ApiResponse.success("Student unregistered successfully from trip"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ==================== STATUS MANAGEMENT ENDPOINTS ====================

    // Put trip on hold
    @PutMapping("/{tripId}/hold")
    public ResponseEntity<ApiResponse<Trip>> holdTrip(@PathVariable String tripId) {
        try {
            Trip trip = tripService.holdTrip(tripId);
            return ResponseEntity.ok(ApiResponse.success(trip, "Trip put on hold successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // Activate trip
    @PutMapping("/{tripId}/activate")
    public ResponseEntity<ApiResponse<Trip>> activateTrip(@PathVariable String tripId) {
        try {
            Trip trip = tripService.activateTrip(tripId);
            return ResponseEntity.ok(ApiResponse.success(trip, "Trip activated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ==================== IMAGE MANAGEMENT ENDPOINTS ====================

    // Upload/Update trip image
    @PutMapping("/{tripId}/image")
    public ResponseEntity<ApiResponse<Trip>> updateTripImage(
            @PathVariable String tripId,
            @RequestBody Map<String, String> body) {
        try {
            String imageData = body.get("imageData");
            if (imageData == null || imageData.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("imageData is required"));
            }
            Trip trip = tripService.updateTripImage(tripId, imageData);
            return ResponseEntity.ok(ApiResponse.success(trip, "Trip image updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ==================== REPORTING ENDPOINTS ====================

    // Get paid students for a trip, grouped by grade
    @GetMapping("/{tripId}/paid-students")
    public ResponseEntity<ApiResponse<Map<String, List<Student>>>> getPaidStudentsByGrade(
            @PathVariable String tripId) {
        try {
            Map<String, List<Student>> studentsByGrade = tripService.getPaidStudentsByGrade(tripId);
            return ResponseEntity.ok(ApiResponse.success(studentsByGrade));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}