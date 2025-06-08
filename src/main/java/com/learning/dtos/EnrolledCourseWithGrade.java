package com.learning.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EnrolledCourseWithGrade {
    private String courseCode;
    private String courseTitle;
    private String courseDescription;
    private String grade;
    private String instructorEmail;
}
