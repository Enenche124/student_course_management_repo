package com.learning.controllers;

import com.learning.data.models.Course;
import com.learning.dtos.studentsSumary.StudentSummary;
import com.learning.services.InstructorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/instructor")
@PreAuthorize("hasRole('INSTRUCTOR')")
public class InstructorController {

    private final InstructorService instructorService;

    @Autowired
    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countInstructors() {
        try {
            return ResponseEntity.ok(instructorService.count());
        } catch (Exception e) {
            throw new RuntimeException("Failed to count instructors: " + e.getMessage());
        }
    }

    @PostMapping("/create-course")
    public ResponseEntity<Course> createCourse(@Valid @RequestBody Course course) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            return ResponseEntity.ok(instructorService.createCourse(course, email));
        } catch (Exception e) {
            throw new RuntimeException("Course creation failed: " + e.getMessage());
        }
    }

    @PostMapping("/assign-grade")
    public ResponseEntity<String> assignGrade(@RequestParam String studentEmail,
                                              @RequestParam String courseCode,
                                              @RequestParam String grade) {
        try {
            instructorService.assignGradeToStudent(studentEmail, courseCode, grade);
            return ResponseEntity.ok("Grade assigned successfully");
        } catch (Exception e) {
            throw new RuntimeException("Grade assignment failed: " + e.getMessage());
        }
    }

    @GetMapping("/students")
    public ResponseEntity<List<StudentSummary>> viewAllEnrolledStudents() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            return ResponseEntity.ok(instructorService.viewAllEnrolledStudents(email));
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve enrolled students: " + e.getMessage());
        }
    }

    @GetMapping("/courses")
    public ResponseEntity<List<Course>> viewAllCourses() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            return ResponseEntity.ok(instructorService.viewAllCourses(email));
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve courses: " + e.getMessage());
        }
    }
}