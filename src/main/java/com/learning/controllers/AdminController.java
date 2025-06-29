package com.learning.controllers;

import com.learning.data.models.Admin;
import com.learning.data.repositories.AdminRepository;
import com.learning.dtos.requests.CreateCourseRequest;
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
    private final AdminRepository adminRepository;

    @Autowired
    public AdminController(AdminService adminService, AdminRepository adminRepository) {
        this.adminService = adminService;
        this.adminRepository = adminRepository;
    }


    @PostMapping("/courses")
    public ResponseEntity<String> createCourse(@RequestBody CreateCourseRequest request) {
        adminService.createCourse(request.getCourseCode(), request.getTitle(), request.getDescription());
        return ResponseEntity.ok("Course created successfully.");
    }


    @PutMapping("/courses/{courseCode}/assign")
    public ResponseEntity<String> assignCourseToLecturer(
            @PathVariable String courseCode,
            @RequestParam String lecturerEmail) {

        adminService.assignCourseToLecturer(courseCode, lecturerEmail);
        return ResponseEntity.ok("Course assigned to lecturer successfully.");
    }


    @GetMapping("/lecturers")
    public ResponseEntity<List<String>> viewAllLecturers() {
        return ResponseEntity.ok(adminService.viewAllLecturerEmails());
    }

    @GetMapping("/courses")
    public  ResponseEntity<List<com.learning.data.models.Course>> viewAllCourses() {
        try {
            return ResponseEntity.ok(adminService.viewAllCourses());
        }catch (Exception e) {
            throw new RuntimeException("Failed to retrieve courses: " + e.getMessage());
        }
    }


    @GetMapping("/{email}")
    public ResponseEntity<Admin> getAdminByEmail(@PathVariable String email) {
        Admin admin = adminRepository.findByEmail(email);
            try {
                return ResponseEntity.ok(admin);
            }catch (Exception e) {
                throw new RuntimeException("No admin found: " + e.getMessage());
            }
    }

    @DeleteMapping("/delete-lecturer")
    public ResponseEntity<?> deleteLecturerByEmail(@RequestParam String email) {
        try {
            adminService.deleteLecturerByEmail(email);
            return ResponseEntity.ok("Lecturer deleted successfully.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete lecturer: " + e.getMessage());
        }
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
}
