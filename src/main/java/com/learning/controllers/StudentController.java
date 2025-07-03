package com.learning.controllers;

import com.learning.data.models.Course;
import com.learning.data.models.User;
import com.learning.dtos.EnrolledCourseWithGrade;
import com.learning.dtos.studentsSumary.StudentPerformanceDto;
import com.learning.services.StudentService;
import jakarta.validation.constraints.NotBlank;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    private final StudentService studentService;

    private static final Logger logger = LogManager.getLogger(StudentController.class);
    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countStudents() {
        try {
            return ResponseEntity.ok(studentService.count());
        } catch (Exception e) {
            throw new RuntimeException("Failed to count students: " + e.getMessage());
        }
    }

    @GetMapping("/available-courses")
    public ResponseEntity<List<Course>> viewAllAvailableCourses() {
        try {
            return ResponseEntity.ok(studentService.viewAllAvailableCourses());
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve available courses: " + e.getMessage());
        }
    }

    @GetMapping("/enrolled-courses")
    public ResponseEntity<List<EnrolledCourseWithGrade>> viewEnrolledCourses() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            return ResponseEntity.ok(studentService.viewEnrolledCourses(email));
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve enrolled courses: " + e.getMessage());
        }
    }

    @PostMapping("/enroll")
    public ResponseEntity<String> enrollInCourse(@RequestParam @NotBlank String courseCode) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info(email);
        try {
            studentService.enrollForCourse(email, courseCode);
            return ResponseEntity.ok("Enrollment successful");
        } catch (Exception e) {
            throw new RuntimeException("Enrollment failed: " + e.getMessage());
        }
    }

    @GetMapping("/performance")
    public ResponseEntity<List<StudentPerformanceDto>> getPerformance() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(studentService.getStudentPerformance(email));
    }

    @GetMapping("/{email:.+}")
    public ResponseEntity<User> getStudentByEmail(@PathVariable String email) {
        return studentService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}