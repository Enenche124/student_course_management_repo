package com.learning.services;

import com.learning.data.models.Instructor;
import com.learning.data.models.Role;
import com.learning.data.models.Student;
import com.learning.data.repositories.InstructorRepository;
import com.learning.data.repositories.StudentRepository;
import com.learning.dtos.requests.LoginRequest;
import com.learning.dtos.requests.RegisterRequest;
import com.learning.dtos.responses.LoginResponse;
import com.learning.dtos.responses.RegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.learning.utils.Mapper.*;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JWTService jwtService;
    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationServiceImpl(JWTService jwtService,
                                     StudentRepository studentRepository,
                                     InstructorRepository instructorRepository,
                                     BCryptPasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.studentRepository = studentRepository;
        this.instructorRepository = instructorRepository;
        this.passwordEncoder = passwordEncoder;
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

        boolean emailExistsInStudent = studentRepository.findStudentByEmail(email).isPresent();
        boolean emailExistsInInstructor = instructorRepository.findByEmail(email).isPresent();

        if (emailExistsInStudent || emailExistsInInstructor) {
            return new RegisterResponse("Email already in use", false);
        }

        switch (role) {
            case STUDENT:
                Student student = mapToStudent(registerRequest);
                student.setPassword(hashedPassword);
                studentRepository.save(student);
                return mapRegister("Student registered successfully", true);

            case INSTRUCTOR:
                Instructor instructor = mapToInstructor(registerRequest);
                instructor.setPassword(hashedPassword);
                instructorRepository.save(instructor);
                return mapRegister("Instructor registered successfully", true);

            default:
                return new RegisterResponse("Invalid role specified", false);
        }
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        String email = loginRequest.getEmail().toLowerCase();
        String password = loginRequest.getPassword();

        Optional<Student> studentOpt = studentRepository.findStudentByEmail(email);
        if (studentOpt.isPresent() && passwordEncoder.matches(password, studentOpt.get().getPassword())) {
            String token = jwtService.generateToken(email, Role.STUDENT);
            return mapLogin("Student logged in successfully", true, token);
        }

        Optional<Instructor> instructorOpt = instructorRepository.findByEmail(email);
        if (instructorOpt.isPresent() && passwordEncoder.matches(password, instructorOpt.get().getPassword())) {
            String token = jwtService.generateToken(email, Role.INSTRUCTOR);
            return mapLogin("Instructor logged in successfully", true, token);
        }

        return mapLogin("Invalid credentials", false, null);
    }
}