package com.practice.services;

import com.practice.dtos.requests.LoginRequest;
import com.practice.dtos.requests.RegisterRequest;
import com.practice.dtos.responses.LoginResponse;
import com.practice.dtos.responses.RegisterResponse;

public interface AuthenticationService {
    RegisterResponse register(RegisterRequest registerRequest);
    LoginResponse login(LoginRequest loginRequest);
}
