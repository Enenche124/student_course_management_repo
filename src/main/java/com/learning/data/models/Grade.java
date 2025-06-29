package com.learning.data.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "grades")
public class Grade {

    @Id
    private String id;

    private String studentEmail;
    private String courseCode;
    private double score;
    private String grade;
    private int position;
}
