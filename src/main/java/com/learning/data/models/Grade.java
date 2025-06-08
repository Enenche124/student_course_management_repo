package com.learning.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "grades")
public class Grade {
    @Id
    private String id;

    private String studentEmail; // Not a key, just a field
    private String courseCode;   // Links to the Course
    private String grade;        // The assigned grade

    // Constructors, getters, setters...
    public Grade(String studentEmail, String courseCode, String grade) {
        this.studentEmail = studentEmail;
        this.courseCode = courseCode;
        this.grade = grade;
    }
}