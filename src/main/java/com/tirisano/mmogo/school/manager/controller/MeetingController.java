package com.tirisano.mmogo.school.manager.controller;

import com.tirisano.mmogo.school.manager.dto.ApiResponse;
import com.tirisano.mmogo.school.manager.model.Meeting;
import com.tirisano.mmogo.school.manager.service.MeetingService;
import com.tirisano.mmogo.school.manager.util.TimestampUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.google.cloud.Timestamp;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Meeting>>> getAllMeetings() {
        try {
            List<Meeting> meetings = meetingService.findAll();
            return ResponseEntity.ok(ApiResponse.success(meetings));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Meeting>> createMeeting(@Valid @RequestBody Meeting meeting) {
        try {
            Meeting savedMeeting = meetingService.createMeeting(meeting);
            return ResponseEntity.ok(ApiResponse.success(savedMeeting, "Meeting scheduled successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<ApiResponse<List<Meeting>>> getParentMeetings(@PathVariable String parentId) {
        try {
            List<Meeting> meetings = meetingService.findByParentId(parentId);
            return ResponseEntity.ok(ApiResponse.success(meetings));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/request-one-on-one")
    public ResponseEntity<ApiResponse<Meeting>> requestOneOnOneMeeting(@RequestBody Map<String, Object> request) {
        try {
            String parentId = (String) request.get("parentId");
            String teacherId = (String) request.get("teacherId");
            String teacherName = (String) request.get("teacherName");
            String parentName = (String) request.get("parentName");
            String title = (String) request.get("title");
            String description = (String) request.get("description");
            String scheduledTimeStr = (String) request.get("scheduledTime");

            // Use TimestampUtil to handle datetime-local format
            Timestamp scheduledTime = TimestampUtil.fromIsoString(scheduledTimeStr);

            Meeting meeting = meetingService.requestOneOnOneMeeting(
                    parentId, teacherId, title, description, scheduledTime, teacherName, parentName
            );
            return ResponseEntity.ok(ApiResponse.success(meeting, "One-on-one meeting request submitted for approval"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{meetingId}")
    public ResponseEntity<ApiResponse<Meeting>> getMeetingById(@PathVariable String meetingId) {
        try {
            Meeting meeting = meetingService.findById(meetingId);
            if (meeting == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(ApiResponse.success(meeting));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{meetingId}")
    public ResponseEntity<ApiResponse<Meeting>> updateMeeting(
            @PathVariable String meetingId,
            @Valid @RequestBody Meeting meeting) {
        try {
            Meeting updatedMeeting = meetingService.updateMeeting(meetingId, meeting);
            return ResponseEntity.ok(ApiResponse.success(updatedMeeting, "Meeting updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{meetingId}")
    public ResponseEntity<ApiResponse<Void>> deleteMeeting(@PathVariable String meetingId) {
        try {
            meetingService.deleteMeeting(meetingId);
            return ResponseEntity.ok(ApiResponse.success(null, "Meeting deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ==================== ADMIN APPROVAL ENDPOINTS ====================

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<Meeting>>> getPendingMeetings() {
        try {
            List<Meeting> meetings = meetingService.findPendingMeetings();
            return ResponseEntity.ok(ApiResponse.success(meetings));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/approved")
    public ResponseEntity<ApiResponse<List<Meeting>>> getApprovedMeetings() {
        try {
            List<Meeting> meetings = meetingService.findApprovedMeetings();
            return ResponseEntity.ok(ApiResponse.success(meetings));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/rejected")
    public ResponseEntity<ApiResponse<List<Meeting>>> getRejectedMeetings() {
        try {
            List<Meeting> meetings = meetingService.findRejectedMeetings();
            return ResponseEntity.ok(ApiResponse.success(meetings));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{meetingId}/approve")
    public ResponseEntity<ApiResponse<Meeting>> approveMeeting(@PathVariable String meetingId) {
        try {
            Meeting meeting = meetingService.approveMeeting(meetingId);
            return ResponseEntity.ok(ApiResponse.success(meeting, "Meeting approved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{meetingId}/reject")
    public ResponseEntity<ApiResponse<Meeting>> rejectMeeting(
            @PathVariable String meetingId,
            @RequestBody Map<String, String> body) {
        try {
            String reason = body.get("reason");
            if (reason == null || reason.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Rejection reason is required"));
            }
            Meeting meeting = meetingService.rejectMeeting(meetingId, reason);
            return ResponseEntity.ok(ApiResponse.success(meeting, "Meeting rejected successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}