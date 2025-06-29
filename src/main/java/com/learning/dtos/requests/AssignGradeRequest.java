package com.learning.dtos.requests;

import lombok.Data;

@Data
public class AssignGradeRequest {
    private String studentEmail;
    private String courseCode;
    private double score;
}
