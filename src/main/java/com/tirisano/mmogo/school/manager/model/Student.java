package com.tirisano.mmogo.school.manager.model;

import com.google.protobuf.DescriptorProtos;
import com.tirisano.mmogo.school.manager.enums.Gender;
import com.tirisano.mmogo.school.manager.enums.StudentStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Student {
    private String studentId;
    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private Timestamp dateOfBirth;
    @NotBlank
    private String birthCertificateId; // Unique identifier for duplicate prevention
    @NotBlank
    private String nationality;
    @NotBlank
    private String grade;
    private Integer yearOfAdmission;
    private String previousSchool; // Optional
    private String latestSchoolReport; // Optional - URL or file reference

    // Existing fields
    private String parentId;
    private String className;
    private String teacher;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private StudentStatus status = StudentStatus.PENDING;
    private String rejectionReason;
    @Builder.Default
    private List<Grade> grades = new ArrayList<>();
    @Builder.Default
    private Timestamp createdAt = Timestamp.now();

    // Helper method for fullName compatibility
    public String getFullName() {
        return name + " " + surname;
    }
}