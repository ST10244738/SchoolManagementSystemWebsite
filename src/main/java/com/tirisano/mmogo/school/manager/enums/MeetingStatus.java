package com.tirisano.mmogo.school.manager.enums;

public enum MeetingStatus {
    PENDING,      // Waiting for admin approval
    APPROVED,     // Approved by admin and scheduled
    REJECTED,     // Rejected by admin (teacher not available)
    SCHEDULED,    // Legacy status, same as APPROVED
    COMPLETED,    // Meeting has been completed
    CANCELLED     // Meeting was cancelled
}
