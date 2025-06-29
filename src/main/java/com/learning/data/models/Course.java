package com.learning.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;

@Data
@Document(collection = "courses")
public class Course {
    @Id
    private String id;

    @NotBlank
    private String courseCode;

    @NotBlank
    private String courseTitle;

    private String courseDescription;

//    @NotBlank
    private String courseInstructorEmail;

    // No grades map needed
}