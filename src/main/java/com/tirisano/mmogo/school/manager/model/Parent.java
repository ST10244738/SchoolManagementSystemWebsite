package com.tirisano.mmogo.school.manager.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.google.cloud.Timestamp;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parent {
    private String parentId;
    private String uid;
    @NotBlank
    private String fullName;
    @Email
    private String email;
    private String phoneNumber;
    private String address;
    @Builder.Default
    private List<String> childrenIds = new ArrayList<>();
    @Builder.Default
    private Timestamp createdAt = Timestamp.now();
}