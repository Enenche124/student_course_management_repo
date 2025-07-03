package com.learning.services;

import com.learning.data.models.*;
import com.learning.data.repositories.UserRepository;
import com.learning.dtos.requests.LoginRequest;
import com.learning.dtos.requests.RegisterRequest;
import com.learning.dtos.responses.LoginResponse;
import com.learning.dtos.responses.RegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JWTService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public AuthenticationServiceImpl(JWTService jwtService,
                                     BCryptPasswordEncoder passwordEncoder,
                                     UserRepository userRepository) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+[a-zA-Z]{2,}$";
        return email != null && email.matches(emailRegex);
    }

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        String email = registerRequest.getEmail().toLowerCase();
        Role role = registerRequest.getRole();


        String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());

        if (!isValidEmail(email)) {
            return new RegisterResponse("Invalid email format", false);
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return new RegisterResponse("Email already in use", false);
        }

        if (role != Role.STUDENT) {
            return new RegisterResponse("Only STUDENT registration is allowed here", false);
        }

        User user = new User();
        user.setEmail(email);
        user.setName(registerRequest.getName());
        user.setPassword(hashedPassword);
        user.setRole(Role.STUDENT);
        userRepository.save(user);

        return new RegisterResponse("Student registered successfully", true);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        String email = loginRequest.getEmail().toLowerCase();
        String password = loginRequest.getPassword();

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return new LoginResponse("Invalid credentials", false, null, null, null);
        }

        User user = userOpt.get();
        String token = jwtService.generateToken(user.getEmail(), user.getRole());

        return new LoginResponse(user.getRole() + " logged in successfully", true, token, user.getRole().name(), user.getName());
    }
}


