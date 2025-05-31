package com.practice.services;

import com.practice.data.models.Student;
import com.practice.data.repositories.StudentRepository;
import com.practice.dtos.requests.LoginRequest;
import com.practice.dtos.requests.RegisterRequest;
import com.practice.dtos.responses.LoginResponse;
import com.practice.dtos.responses.RegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.practice.utils.Mapper.*;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final StudentRepository studentRepository;
    @Autowired
    public AuthenticationServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        Optional<Student> exist = studentRepository.findStudentByEmail(registerRequest.getEmail());
        if (exist.isPresent()) {
            return new RegisterResponse("Email already in use", false);
        }

        Student student = map(registerRequest);
        studentRepository.save(student);
        return  mapRegister("Registered successfully", true);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Optional<Student> studentOptional =  studentRepository.findStudentByEmail(loginRequest.getEmail());



        if (studentOptional.isEmpty()) {
            return new LoginResponse("No user found", false);
        }

        Student student = studentOptional.get();
        if (!student.getPassword().equals(loginRequest.getPassword())) {
            return new LoginResponse("Wrong password", false);
        }

        return mapLogin("Logged in successfully", true);
    }
}
