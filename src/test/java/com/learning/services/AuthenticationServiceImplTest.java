package com.learning.services;

import com.learning.data.models.Role;
import com.learning.data.repositories.LecturerRepository;
import com.learning.data.repositories.StudentRepository;
import com.learning.dtos.requests.LoginRequest;
import com.learning.dtos.requests.RegisterRequest;
import com.learning.dtos.responses.LoginResponse;
import com.learning.dtos.responses.RegisterResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AuthenticationServiceImplTest {

    @Autowired
    private AuthenticationServiceImpl authenticationService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private LecturerRepository lecturerRepository;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        lecturerRepository.deleteAll();
    }

    @Test
    public void testStudentRegisterSuccess() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("student@example.com");
        request.setPassword("Password@1236");
        request.setName("John Doe");
        request.setRole(Role.STUDENT);

        RegisterResponse response = authenticationService.register(request);
        assertTrue(response.isSuccess());
        assertEquals("Student registered successfully", response.getMessage());
    }

    @Test
    public void testInstructorRegisterSuccess() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("instructor@example.com");
        request.setPassword("Password@1236");
        request.setName("Prof. Smith");
        request.setRole(Role.LECTURER);

        RegisterResponse response = authenticationService.register(request);
        assertTrue(response.isSuccess());
        assertEquals("Instructor registered successfully", response.getMessage());
    }

    @Test
    public void testDuplicateEmailRegistrationFails() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("duplicate@example.com");
        request.setPassword("Goodpassword@12");
        request.setName("Mike Faraday");
        request.setRole(Role.STUDENT);

        RegisterResponse first = authenticationService.register(request);
        RegisterResponse second = authenticationService.register(request);

        assertTrue(first.isSuccess());
        assertFalse(second.isSuccess());
        assertEquals("Email already in use", second.getMessage());
    }

    @Test
    public void testRegisterInvalidEmailFormatFails() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("bad-email");
        request.setPassword("Password#22");
        request.setName("Invalid User");
        request.setRole(Role.STUDENT);

        RegisterResponse response = authenticationService.register(request);
        assertFalse(response.isSuccess());
        assertEquals("Invalid email format", response.getMessage());
    }

    @Test
    public void testStudentLoginSuccess() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("niko@example.com");
        registerRequest.setPassword("Secret@123");
        registerRequest.setName("Niko Gee");
        registerRequest.setRole(Role.STUDENT);
        RegisterResponse registerResponse = authenticationService.register(registerRequest);
        assertTrue(registerResponse.isSuccess());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(registerRequest.getEmail());
        loginRequest.setPassword(registerRequest.getPassword());
        LoginResponse response = authenticationService.login(loginRequest);
        assertTrue(response.isSuccess());
        assertEquals("Student logged in successfully", response.getMessage());
    }

    @Test
    public void testInstructorLoginSuccess() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("instructor1@example.com");
        registerRequest.setPassword("Techpass@11");
        registerRequest.setName("Instructor Jerry");
        registerRequest.setRole(Role.LECTURER);
        RegisterResponse registerResponse = authenticationService.register(registerRequest);
        assertNotNull(registerResponse);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(registerRequest.getEmail());
        loginRequest.setPassword(registerRequest.getPassword());
        LoginResponse response = authenticationService.login(loginRequest);
        assertTrue(response.isSuccess());
        assertEquals("Instructor logged in successfully", response.getMessage());
    }

    @Test
    public void testLoginWithWrongPasswordFails() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("daniel@example.com");
        registerRequest.setPassword("rightpass@22");
        registerRequest.setName("Daniel Joe");
        registerRequest.setRole(Role.STUDENT);
        authenticationService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(registerRequest.getEmail());
        loginRequest.setPassword("wrongpass");

        LoginResponse response = authenticationService.login(loginRequest);
        assertFalse(response.isSuccess());
        assertEquals("Invalid credentials", response.getMessage());
    }


    @Test
    public void testCaseInsensitiveEmailRegistrationFails() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("Student@Example.com");
        request.setPassword("Password@123");
        request.setName("John Doe");
        request.setRole(Role.STUDENT);
        authenticationService.register(request);

        RegisterRequest duplicate = new RegisterRequest();
        duplicate.setEmail("student@example.com");
        duplicate.setPassword("Password@123");
        duplicate.setName("Jane Doe");
        duplicate.setRole(Role.STUDENT);

        RegisterResponse response = authenticationService.register(duplicate);
        assertFalse(response.isSuccess());
        assertEquals("Email already in use", response.getMessage());
    }

    @Test
    public void testCaseInsensitiveEmailLogin() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("Test@Example.com");
        registerRequest.setPassword("Password@123");
        registerRequest.setName("Test User");
        registerRequest.setRole(Role.STUDENT);
        authenticationService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("Password@123");
        LoginResponse response = authenticationService.login(loginRequest);
        assertTrue(response.isSuccess());
        assertEquals("Student logged in successfully", response.getMessage());
    }
}