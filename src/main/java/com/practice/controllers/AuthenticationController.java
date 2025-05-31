package com.practice.controllers;


import com.practice.dtos.requests.LoginRequest;
import com.practice.dtos.requests.RegisterRequest;
import com.practice.dtos.responses.LoginResponse;
import com.practice.dtos.responses.RegisterResponse;
import com.practice.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest registerRequest) {
        return authenticationService.register(registerRequest);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        return authenticationService.login(loginRequest);
    }

    @GetMapping("/test")
    public String test() {
        return "Auth Controller is working!";
    }

}
