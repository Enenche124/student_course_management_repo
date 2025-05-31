package com.practice.dtos.responses;

import com.practice.data.models.Student;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
//    private String token;
    private String message;
    private boolean success;
//    private Student student;
}
