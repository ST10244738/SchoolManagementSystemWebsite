package com.tirisano.mmogo.school.manager.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.google.cloud.Timestamp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {
    private String tripId;
    @NotBlank
    private String title;
    private String description;
    private String destination;
    private String imageUrl;
    @DecimalMin("0.0")
    private BigDecimal price;
    private Timestamp tripDate;
    @Builder.Default
    private List<String> eligibleGrades = new ArrayList<>();
    @Builder.Default
    private List<String> registeredStudents = new ArrayList<>();
    @Builder.Default
    private boolean active = true;
    @Builder.Default
    private Timestamp createdAt = Timestamp.now();
}