package com.learning.services;

import com.learning.data.models.*;
import com.learning.data.repositories.CourseRepository;
import com.learning.data.repositories.UserRepository;
import com.learning.dtos.requests.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    @Autowired
    public AdminServiceImpl(CourseRepository courseRepository,
                             UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.courseRepository = courseRepository;

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Course createCourse(String courseCode, String title, String description) {
        if (courseCode == null || courseCode.isBlank() ||
                title == null || title.isBlank()) {
            throw new IllegalArgumentException("Course code and title are required");
        }

        if (courseRepository.findByCourseCode(courseCode).isPresent()) {
            throw new IllegalArgumentException("Course with this code already exists");
        }

        Course course = new Course();
        course.setCourseCode(courseCode);
        course.setCourseTitle(title);
        course.setCourseDescription(description);

        courseRepository.save(course);
        return course;
    }

    @Override
    public void assignCourseToLecturer(String courseCode, String lecturerEmail) {
        Course course = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        User lecturer = userRepository.findByEmail(lecturerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Lecturer not found"));

        if (lecturerEmail.equalsIgnoreCase(course.getCourseLecturerEmail())) {
            throw new IllegalStateException("This course is already assigned to this lecturer");
        }

        course.setCourseLecturerEmail(lecturer.getEmail());
        courseRepository.save(course);
    }



    @Override
    public List<String> viewAllLecturerEmails() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.LECTURER)
                .map(User::getEmail)
                .collect(Collectors.toList());
    }


    @Override
    public List<Course> viewAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public void deleteLecturerByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Lecturer not found"));

        if (user.getRole() != Role.LECTURER) {
            throw new RuntimeException("User is not a lecturer");
        }

        userRepository.delete(user);
    }





    @Override
    public void deleteCourseByCourseCode(String courseCode) {
        Course course = courseRepository.findByCourseCode(courseCode).orElseThrow(() -> new IllegalArgumentException("Course not found"));
        courseRepository.delete(course);
    }


    @Override
    public ResponseEntity<?> createUser(RegisterRequest request) {
        String email = request.getEmail().toLowerCase();

        if (request.getRole() == Role.STUDENT) {
            return ResponseEntity.badRequest().body("Admin cannot create students.");
        }

        if (!isValidEmail(email)) {
            return ResponseEntity.badRequest().body("Invalid email format.");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body("Email already in use.");
        }

        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(request.getRole());
        newUser.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());

        userRepository.save(newUser);
        return ResponseEntity.ok(request.getRole().name() + " created successfully");
    }


    @Override
    public ResponseEntity<?> getAdminByEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email).filter(u -> u.getRole() == Role.ADMIN);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Admin not found.");
        }
        return ResponseEntity.ok(userOpt.get());
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+[a-zA-Z]{2,}$";
        return email != null && email.matches(emailRegex);
    }



}
