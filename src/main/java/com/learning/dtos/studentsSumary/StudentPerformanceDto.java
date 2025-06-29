package com.learning.dtos.studentsSumary;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentPerformanceDto {
    private String courseCode;
    private String courseTitle;
    private double score;
    private String grade;
    private int position;
}
