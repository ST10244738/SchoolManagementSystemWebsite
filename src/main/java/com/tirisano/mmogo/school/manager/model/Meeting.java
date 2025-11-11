package com.tirisano.mmogo.school.manager.model;

import com.tirisano.mmogo.school.manager.enums.MeetingStatus;
import com.tirisano.mmogo.school.manager.enums.MeetingType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.google.cloud.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Meeting {
    private String meetingId;

    @NotBlank
    private String title;

    private String description;
    private Timestamp scheduledTime;
    private String teacherId;
    private String teacherName;
    private String parentId;

    @Enumerated(EnumType.STRING)
    private MeetingType type;

    @Enumerated(EnumType.STRING)
    private MeetingStatus status;  // Remove @Builder.Default

    private Timestamp createdAt;  // Remove @Builder.Default

    private String rejectionReason;  // Reason if meeting is rejected
    private String parentName;  // Name of parent who requested the meeting
}