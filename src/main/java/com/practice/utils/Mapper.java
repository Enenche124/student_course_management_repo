package com.practice.utils;

import com.practice.data.models.Student;
import com.practice.dtos.requests.RegisterRequest;
import com.practice.dtos.responses.LoginResponse;
import com.practice.dtos.responses.RegisterResponse;

public class Mapper {

    public static Student map(RegisterRequest  registerRequest) {
        Student student = new Student();
        student.setEmail(registerRequest.getEmail());
        student.setPassword(registerRequest.getPassword());
        student.setRole(registerRequest.getRole());
        student.setName(registerRequest.getName());
        return student;
    }

    public static RegisterResponse mapRegister(String message, boolean success) {
        return new RegisterResponse(message, success);
    }

    public static LoginResponse mapLogin(String message, boolean success) {
        return new LoginResponse(message, success);
    }
}
