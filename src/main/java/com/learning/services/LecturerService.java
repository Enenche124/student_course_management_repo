package com.learning.services;

import com.learning.data.models.Course;
import com.learning.dtos.studentsSumary.StudentSummary;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LecturerService {
    long count();
    void assignGradeToStudent(String studentEmail, String courseCode, double score);
    List<StudentSummary> viewAllEnrolledStudents(String instructorEmail);
    List<Course> viewAllCourses(String instructorEmail);
    ResponseEntity<?> getLecturerByEmail(String email);

}