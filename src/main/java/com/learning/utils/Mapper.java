package com.learning.utils;

import com.learning.data.models.Admin;
import com.learning.data.models.Lecturer;
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

    public static Lecturer mapToLecturer(RegisterRequest request) {
        Lecturer lecturer = new Lecturer();
        lecturer.setName(request.getName());
        lecturer.setEmail(request.getEmail().toLowerCase());
        lecturer.setRole(Role.LECTURER);
        return lecturer;
    }

    public static Admin mapToAdmin(RegisterRequest request){
        Admin admin = new Admin();
        admin.setName(request.getName());
        admin.setEmail(request.getEmail().toLowerCase());
        admin.setRole(Role.ADMIN);
        return admin;
    }

    public static RegisterResponse mapRegister(String message, boolean success) {
        return new RegisterResponse(message, success);
    }

    public static LoginResponse mapLogin(String message, boolean success, String token, String  role    ) {
        return new LoginResponse(message, success, token, role);
    }
}