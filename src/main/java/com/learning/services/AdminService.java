package com.learning.services;

import com.learning.data.models.Course;
import com.learning.dtos.requests.RegisterRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AdminService {

    Course createCourse(String courseCode, String title, String description);

    void assignCourseToLecturer(String courseCode, String lecturerEmail);
    List<String> viewAllLecturerEmails();
    List<Course> viewAllCourses();
    void deleteLecturerByEmail(String email);
    void deleteCourseByCourseCode(String courseCode);
    ResponseEntity<?> createUser(RegisterRequest request);
//    ResponseEntity<?> deleteLecturerByEmail(String email);
    ResponseEntity<?> getAdminByEmail(String email);




}
