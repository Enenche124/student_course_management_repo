package com.learning.controllers;


import com.learning.data.models.Course;
import com.learning.dtos.requests.CreateCourseRequest;
import com.learning.dtos.requests.RegisterRequest;
import com.learning.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;


    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;

    }


    @PostMapping("/courses")
    public ResponseEntity<String> createCourse(@RequestBody CreateCourseRequest request) {
        try {
            adminService.createCourse(request.getCourseCode(), request.getTitle(), request.getDescription());
            return ResponseEntity.ok("Course created successfully.");
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create course: " + e.getMessage());
        }
    }


    @PutMapping("/courses/{courseCode}/assign")
    public ResponseEntity<String> assignCourseToLecturer(
            @PathVariable String courseCode,
            @RequestParam String lecturerEmail) {

        try {
            adminService.assignCourseToLecturer(courseCode, lecturerEmail);
            return ResponseEntity.ok("Course assigned to lecturer successfully.");
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to assign course to lecturer: " + e.getMessage());
        }

    }


    @GetMapping("/lecturers")
    public ResponseEntity<List<String>> viewAllLecturers() {
        return ResponseEntity.ok(adminService.viewAllLecturerEmails());
    }

    @GetMapping("/courses")
    public  ResponseEntity<List<Course>> viewAllCourses() {
        try {
            return ResponseEntity.ok(adminService.viewAllCourses());
        }catch (Exception e) {
            throw new RuntimeException("Failed to retrieve courses: " + e.getMessage());
        }
    }


    @GetMapping("/{email}")
    public ResponseEntity<?> getAdminByEmail(@PathVariable String email) {
        try {
            return adminService.getAdminByEmail(email);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to retrieve admin: " + e.getMessage());
        }

    }


    @DeleteMapping("/delete-lecturer")
    public ResponseEntity<?> deleteLecturerByEmail(@RequestParam String email) {
        try {
            adminService.deleteLecturerByEmail(email);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete lecturer: " + e.getMessage());
        }
        return ResponseEntity.ok("Lecturer deleted successfully.");
    }


    @DeleteMapping("/delete-course")
    public ResponseEntity<?> deleteCourseByCourseCode(@RequestParam String courseCode) {
        try {
            adminService.deleteCourseByCourseCode(courseCode);
            return ResponseEntity.ok("Course deleted successfully.");
        }catch (Exception e) {
            throw new RuntimeException("Failed to delete course: " + e.getMessage());
        }
    }


    @PostMapping("/create-user")
    public ResponseEntity<?> createUser(@RequestBody RegisterRequest request) {
        try {
            return adminService.createUser(request);
        }catch (Exception e) {
            throw new RuntimeException("Failed to create user: " + e.getMessage());
        }

    }
}
