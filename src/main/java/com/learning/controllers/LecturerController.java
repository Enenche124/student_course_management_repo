package com.learning.controllers;

import com.learning.data.models.Course;


import com.learning.dtos.requests.AssignGradeRequest;
import com.learning.dtos.studentsSumary.StudentSummary;
import com.learning.services.LecturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/lecturer")
@PreAuthorize("hasRole('LECTURER')")
public class LecturerController {

    private final LecturerService lecturerService;


    @Autowired
    public LecturerController(LecturerService lecturerService) {
        this.lecturerService = lecturerService;

    }

    @GetMapping("/count")
    public ResponseEntity<Long> countInstructors() {
        try {
            return ResponseEntity.ok(lecturerService.count());
        } catch (Exception e) {
            throw new RuntimeException("Failed to count instructors: " + e.getMessage());
        }
    }

    @PostMapping("/assign-grade")
    public ResponseEntity<String> assignGrade(@RequestBody AssignGradeRequest request) {
        try {
            lecturerService.assignGradeToStudent(
                    request.getStudentEmail(),
                    request.getCourseCode(),
                    request.getScore()
            );
            return ResponseEntity.ok("Grade assigned successfully");
        } catch (Exception e) {
            throw new RuntimeException("Grade assignment failed: " + e.getMessage());
        }
    }


    @GetMapping("/students")
    public ResponseEntity<List<StudentSummary>> viewAllEnrolledStudents() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            return ResponseEntity.ok(lecturerService.viewAllEnrolledStudents(email));
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve enrolled students: " + e.getMessage());
        }
    }


    @GetMapping("/courses")
    public ResponseEntity<List<Course>> viewAllCourses() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("LECTURER EMAIL FROM CONTEXT: " + email);
        System.out.println("LECTURER EMAIL FROM CONTEXT: " + email);
        System.out.println("LECTURER EMAIL FROM CONTEXT: " + email);// Debug log

        try {
            return ResponseEntity.ok(lecturerService.viewAllCourses(email));
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve courses: " + e.getMessage());
        }
    }



    @GetMapping("/{email}")
    public ResponseEntity<?> getLecturerByEmail(@PathVariable String email) {
        return lecturerService.getLecturerByEmail(email);
    }



}