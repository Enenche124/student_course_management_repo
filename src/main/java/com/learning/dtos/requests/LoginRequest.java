package com.learning.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String password;

    @NotBlank
    private String email;
}