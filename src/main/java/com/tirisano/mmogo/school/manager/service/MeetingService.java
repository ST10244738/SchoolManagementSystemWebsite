package com.tirisano.mmogo.school.manager.service;

import com.tirisano.mmogo.school.manager.enums.MeetingStatus;
import com.tirisano.mmogo.school.manager.enums.MeetingType;
import com.tirisano.mmogo.school.manager.model.Meeting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.google.cloud.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingService {

    private final FirebaseService firebaseService;

    public List<Meeting> findAll() {
        try {
            return firebaseService.findAll("meetings", Meeting.class).join();
        } catch (Exception e) {
            log.error("Error fetching all meetings", e);
            throw new RuntimeException("Failed to fetch meetings: " + e.getMessage());
        }
    }

    public Meeting createMeeting(Meeting meeting) {
        try {
            // Set defaults if not provided
            // Meetings created by admin go directly to SCHEDULED status
            // Meetings requested by parents will have PENDING status set by the controller
            if (meeting.getStatus() == null) {
                meeting.setStatus(MeetingStatus.SCHEDULED);
            }
            if (meeting.getCreatedAt() == null) {
                meeting.setCreatedAt(Timestamp.now());
            }

            log.info("Creating meeting: {}", meeting.getTitle());
            firebaseService.save("meetings", meeting).join();
            // FirebaseService automatically sets meetingId on the meeting object

            log.info("✅ Meeting created successfully with ID: {}", meeting.getMeetingId());
            return meeting;
        } catch (Exception e) {
            log.error("❌ Error creating meeting", e);
            throw new RuntimeException("Failed to create meeting: " + e.getMessage());
        }
    }

    public List<Meeting> findByParentId(String parentId) {
        try {
            // Get all meetings
            List<Meeting> allMeetings = firebaseService.findAll("meetings", Meeting.class).join();

            // Filter to show:
            // 1. ONE_ON_ONE meetings where this parent is the requester
            // 2. All GROUP_MEETING meetings (visible to everyone)
            return allMeetings.stream()
                    .filter(meeting -> {
                        if (meeting.getType() == MeetingType.GROUP_MEETING) {
                            return true; // Group meetings visible to all
                        } else if (meeting.getType() == MeetingType.ONE_ON_ONE) {
                            return parentId.equals(meeting.getParentId()); // Only show to requesting parent
                        }
                        return false;
                    })
                    .toList();
        } catch (Exception e) {
            log.error("Error fetching meetings for parent: {}", parentId, e);
            throw new RuntimeException("Failed to fetch meetings: " + e.getMessage());
        }
    }

    public Meeting requestOneOnOneMeeting(String parentId, String teacherId, String title,
                                          String description, Timestamp scheduledTime, String teacherName, String parentName) {
        try {
            log.info("Requesting one-on-one meeting between parent {} and teacher {}", parentId, teacherId);

            Meeting meeting = Meeting.builder()
                    .title(title)
                    .description(description)
                    .scheduledTime(scheduledTime)
                    .teacherId(teacherId)
                    .teacherName(teacherName)
                    .parentId(parentId)
                    .parentName(parentName)
                    .type(MeetingType.ONE_ON_ONE)
                    .status(MeetingStatus.PENDING)  // Set as PENDING for admin approval
                    .createdAt(Timestamp.now())  // Set explicitly here
                    .build();

            return createMeeting(meeting);
        } catch (Exception e) {
            log.error("❌ Error requesting one-on-one meeting", e);
            throw new RuntimeException("Failed to request meeting: " + e.getMessage());
        }
    }

    public Meeting findById(String meetingId) {
        try {
            return firebaseService.findById("meetings", meetingId, Meeting.class).join();
        } catch (Exception e) {
            log.error("Error finding meeting by ID: {}", meetingId, e);
            throw new RuntimeException("Failed to find meeting: " + e.getMessage());
        }
    }

    public Meeting updateMeeting(String meetingId, Meeting meeting) {
        try {
            Meeting existingMeeting = findById(meetingId);
            if (existingMeeting == null) {
                throw new RuntimeException("Meeting not found with ID: " + meetingId);
            }

            meeting.setMeetingId(meetingId);
            // Preserve original creation timestamp
            if (meeting.getCreatedAt() == null) {
                meeting.setCreatedAt(existingMeeting.getCreatedAt());
            }

            firebaseService.save("meetings", meeting, meetingId).join();
            log.info("✅ Meeting updated successfully: {}", meetingId);
            return meeting;
        } catch (Exception e) {
            log.error("❌ Error updating meeting: {}", meetingId, e);
            throw new RuntimeException("Failed to update meeting: " + e.getMessage());
        }
    }

    public void deleteMeeting(String meetingId) {
        try {
            Meeting meeting = findById(meetingId);
            if (meeting == null) {
                throw new RuntimeException("Meeting not found with ID: " + meetingId);
            }

            firebaseService.delete("meetings", meetingId).join();
            log.info("✅ Meeting deleted successfully: {}", meetingId);
        } catch (Exception e) {
            log.error("❌ Error deleting meeting: {}", meetingId, e);
            throw new RuntimeException("Failed to delete meeting: " + e.getMessage());
        }
    }

    // Get pending meetings (for admin approval)
    public List<Meeting> findPendingMeetings() {
        try {
            return firebaseService.findByField("meetings", "status", MeetingStatus.PENDING, Meeting.class).join();
        } catch (Exception e) {
            log.error("Error fetching pending meetings", e);
            throw new RuntimeException("Failed to fetch pending meetings: " + e.getMessage());
        }
    }

    // Get approved meetings
    public List<Meeting> findApprovedMeetings() {
        try {
            return firebaseService.findByField("meetings", "status", MeetingStatus.APPROVED, Meeting.class).join();
        } catch (Exception e) {
            log.error("Error fetching approved meetings", e);
            throw new RuntimeException("Failed to fetch approved meetings: " + e.getMessage());
        }
    }

    // Get rejected meetings
    public List<Meeting> findRejectedMeetings() {
        try {
            return firebaseService.findByField("meetings", "status", MeetingStatus.REJECTED, Meeting.class).join();
        } catch (Exception e) {
            log.error("Error fetching rejected meetings", e);
            throw new RuntimeException("Failed to fetch rejected meetings: " + e.getMessage());
        }
    }

    // Approve meeting
    public Meeting approveMeeting(String meetingId) {
        try {
            Meeting meeting = findById(meetingId);
            if (meeting == null) {
                throw new RuntimeException("Meeting not found with ID: " + meetingId);
            }

            meeting.setStatus(MeetingStatus.APPROVED);
            meeting.setRejectionReason(null); // Clear any previous rejection reason
            firebaseService.save("meetings", meeting, meetingId).join();

            log.info("✅ Meeting approved successfully: {}", meetingId);
            return meeting;
        } catch (Exception e) {
            log.error("❌ Error approving meeting: {}", meetingId, e);
            throw new RuntimeException("Failed to approve meeting: " + e.getMessage());
        }
    }

    // Reject meeting with reason
    public Meeting rejectMeeting(String meetingId, String reason) {
        try {
            Meeting meeting = findById(meetingId);
            if (meeting == null) {
                throw new RuntimeException("Meeting not found with ID: " + meetingId);
            }

            meeting.setStatus(MeetingStatus.REJECTED);
            meeting.setRejectionReason(reason);
            firebaseService.save("meetings", meeting, meetingId).join();

            log.info("✅ Meeting rejected successfully: {}", meetingId);
            return meeting;
        } catch (Exception e) {
            log.error("❌ Error rejecting meeting: {}", meetingId, e);
            throw new RuntimeException("Failed to reject meeting: " + e.getMessage());
        }
    }
}