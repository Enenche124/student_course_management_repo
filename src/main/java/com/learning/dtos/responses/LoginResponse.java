package com.learning.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

    private String message;
    private boolean success;
    private String token;
    private String role;
    private String name;
}