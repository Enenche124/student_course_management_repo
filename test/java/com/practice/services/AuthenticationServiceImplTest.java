package com.practice.services;

import com.practice.data.models.Student;
import com.practice.data.repositories.StudentRepository;
import com.practice.dtos.requests.LoginRequest;
import com.practice.dtos.requests.RegisterRequest;
import com.practice.dtos.responses.LoginResponse;
import com.practice.dtos.responses.RegisterResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AuthenticationServiceImplTest {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
    }

    @Test
    public void testRegister_newStudent_works() {
     RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("majek majek");
        registerRequest.setEmail("majek@gmail.com");
        registerRequest.setPassword("majek123");
        registerRequest.setRole("student");

        RegisterResponse registerResponse = authenticationService.register(registerRequest);

        assertNotNull(registerResponse);
        assertTrue(registerResponse.isSuccess());
        assertEquals("Registered successfully",  registerResponse.getMessage());
    }
    @Test
    public void testThatRegisterThrowsExceptionForDuplicateEmail() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("majek majek");
        registerRequest.setEmail("majek@gmail.com");
        registerRequest.setPassword("majek123");
        registerRequest.setRole("student");

        RegisterResponse registerResponse = authenticationService.register(registerRequest);
        RegisterResponse registerResponseTwo = authenticationService.register(registerRequest);
        assertNotNull(registerResponse);
        assertTrue(registerResponse.isSuccess());

        assertFalse(registerResponseTwo.isSuccess());
        assertEquals("Email already in use", registerResponseTwo.getMessage());
    }

    @Test
    public void testThatLogin_methodWorks(){

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("majek majek");
        registerRequest.setEmail("majek@gmail.com");
        registerRequest.setPassword("majek123");
        registerRequest.setRole("student");

        RegisterResponse registerResponse = authenticationService.register(registerRequest);
        assertNotNull(registerResponse);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(registerRequest.getEmail());
        loginRequest.setPassword(registerRequest.getPassword());

        LoginResponse loginResponse = authenticationService.login(loginRequest);

        assertNotNull(loginResponse);
        assertTrue(loginResponse.isSuccess());
        assertEquals("Logged in successfully", loginResponse.getMessage());
        assertEquals(registerRequest.getEmail(), loginRequest.getEmail());

    }



}