package com.tirisano.mmogo.school.manager.model;

import com.google.cloud.Timestamp;
import com.tirisano.mmogo.school.manager.enums.UserRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private String uid;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String fullName;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Builder.Default
    private Timestamp createdAt = Timestamp.now();

    @Builder.Default
    private boolean active = true;
}