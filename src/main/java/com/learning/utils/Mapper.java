package com.learning.utils;

import com.learning.data.models.Instructor;
import com.learning.data.models.Role;
import com.learning.data.models.Student;
import com.learning.dtos.requests.RegisterRequest;
import com.learning.dtos.responses.LoginResponse;
import com.learning.dtos.responses.RegisterResponse;

public class Mapper {

    public static Student mapToStudent(RegisterRequest request) {
        Student student = new Student();
        student.setName(request.getName());
        student.setEmail(request.getEmail().toLowerCase());
        student.setRole(Role.STUDENT);
        return student;
    }

    public static Instructor mapToInstructor(RegisterRequest request) {
        Instructor instructor = new Instructor();
        instructor.setName(request.getName());
        instructor.setEmail(request.getEmail().toLowerCase());
        instructor.setRole(Role.INSTRUCTOR);
        return instructor;
    }

    public static RegisterResponse mapRegister(String message, boolean success) {
        return new RegisterResponse(message, success);
    }

    public static LoginResponse mapLogin(String message, boolean success, String token) {
        return new LoginResponse(message, success, token);
    }
}