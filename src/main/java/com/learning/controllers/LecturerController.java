package com.learning.controllers;

import com.learning.data.models.Course;
import com.learning.data.models.Lecturer;
import com.learning.data.repositories.LecturerRepository;
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
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LecturerController {

    private final LecturerService lecturerService;
    private final LecturerRepository lecturerRepository;

    @Autowired
    public LecturerController(LecturerService lecturerService, LecturerRepository lecturerRepository) {
        this.lecturerService = lecturerService;
        this.lecturerRepository = lecturerRepository;
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
        try {
            return ResponseEntity.ok(lecturerService.viewAllCourses(email));
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve courses: " + e.getMessage());
        }
    }


    @GetMapping("/{email}")
    public ResponseEntity<Lecturer> getLecturerByEmail(@PathVariable String email) {
        return ResponseEntity.ok(
                lecturerRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Lecturer not found"))
        );
    }

}