package com.tirisano.mmogo.school.manager.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.google.cloud.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade {
    private String subject;
    private String score;
    private String term;
    private Timestamp date;
    private String comments;
}