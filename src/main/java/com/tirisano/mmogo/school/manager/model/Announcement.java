package com.tirisano.mmogo.school.manager.model;

import com.tirisano.mmogo.school.manager.enums.AnnouncementType;
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
public class Announcement {
    private String announcementId;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private AnnouncementType type = AnnouncementType.GENERAL;

    @Builder.Default
    private boolean active = true;

    @Builder.Default
    private Timestamp createdAt = Timestamp.now();
}
