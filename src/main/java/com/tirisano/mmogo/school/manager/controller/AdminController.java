package com.tirisano.mmogo.school.manager.controller;


import com.tirisano.mmogo.school.manager.dto.ApiResponse;
import com.tirisano.mmogo.school.manager.model.Announcement;
import com.tirisano.mmogo.school.manager.model.DocumentRequest;
import com.tirisano.mmogo.school.manager.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/announcements")
    public ResponseEntity<ApiResponse<List<Announcement>>> getAllAnnouncements() {
        try {
            List<Announcement> announcements = adminService.getAllAnnouncements();
            return ResponseEntity.ok(ApiResponse.success(announcements));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/announcements")
    public ResponseEntity<ApiResponse<Announcement>> createAnnouncement(@Valid @RequestBody Announcement announcement) {
        try {
            Announcement savedAnnouncement = adminService.createAnnouncement(announcement);
            return ResponseEntity.ok(ApiResponse.success(savedAnnouncement, "Announcement created"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/announcements/{announcementId}")
    public ResponseEntity<ApiResponse<Announcement>> getAnnouncementById(@PathVariable String announcementId) {
        try {
            Announcement announcement = adminService.getAnnouncementById(announcementId);
            if (announcement == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(ApiResponse.success(announcement));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/announcements/{announcementId}")
    public ResponseEntity<ApiResponse<Announcement>> updateAnnouncement(
            @PathVariable String announcementId,
            @Valid @RequestBody Announcement announcement) {
        try {
            Announcement updatedAnnouncement = adminService.updateAnnouncement(announcementId, announcement);
            return ResponseEntity.ok(ApiResponse.success(updatedAnnouncement, "Announcement updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/announcements/{announcementId}")
    public ResponseEntity<ApiResponse<Void>> deleteAnnouncement(@PathVariable String announcementId) {
        try {
            adminService.deleteAnnouncement(announcementId);
            return ResponseEntity.ok(ApiResponse.success(null, "Announcement deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/document-requests")
    public ResponseEntity<ApiResponse<List<DocumentRequest>>> getAllDocumentRequests() {
        try {
            List<DocumentRequest> requests = adminService.getAllDocumentRequests();
            return ResponseEntity.ok(ApiResponse.success(requests));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/document-requests/pending")
    public ResponseEntity<ApiResponse<List<DocumentRequest>>> getPendingDocumentRequests() {
        try {
            List<DocumentRequest> requests = adminService.getPendingDocumentRequests();
            return ResponseEntity.ok(ApiResponse.success(requests));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/document-requests/{requestId}/approve")
    public ResponseEntity<ApiResponse<DocumentRequest>> approveDocumentRequest(@PathVariable String requestId) {
        try {
            DocumentRequest request = adminService.approveDocumentRequest(requestId);
            return ResponseEntity.ok(ApiResponse.success(request, "Document request approved"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
