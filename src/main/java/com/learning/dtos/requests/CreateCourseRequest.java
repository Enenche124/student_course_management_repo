package com.learning.dtos.requests;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class CreateCourseRequest {
    @Id
    private String id;
    private String courseCode;
    private String title;
    private String description;
}
