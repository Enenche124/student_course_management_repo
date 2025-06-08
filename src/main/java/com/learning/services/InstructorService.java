package com.learning.services;

import com.learning.data.models.Course;
import com.learning.dtos.studentsSumary.StudentSummary;

import java.util.List;

public interface InstructorService {
    long count();
    Course createCourse(Course course, String instructorEmail);
    void assignGradeToStudent(String studentEmail, String courseCode, String grade);
    List<StudentSummary> viewAllEnrolledStudents(String instructorEmail);
    List<Course> viewAllCourses(String instructorEmail);
}