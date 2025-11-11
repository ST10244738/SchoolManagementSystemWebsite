package com.tirisano.mmogo.school.manager.dto;

import com.tirisano.mmogo.school.manager.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private String uid;
    private String email;
    private String fullName;
    private String phoneNumber;
    private UserRole role;
    private String parentId;
}