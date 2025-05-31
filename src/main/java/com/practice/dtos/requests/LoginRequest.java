package com.practice.dtos.requests;

import lombok.Data;

@Data
public class LoginRequest {
    private String password;
    private String email;
}
