package com.tirisano.mmogo.school.manager.dto;

import com.tirisano.mmogo.school.manager.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Size(min = 6)
    private String password;
    @NotBlank
    private String fullName;
    private String phoneNumber;
    private String address;
    @Builder.Default
    private UserRole role = UserRole.PARENT;
}
