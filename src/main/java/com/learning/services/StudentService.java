package com.learning.services;

import com.learning.data.models.Course;
import com.learning.dtos.EnrolledCourseWithGrade;

import java.util.List;

public interface StudentService {
    long count();
    List<Course> viewAllAvailableCourses();
    List<EnrolledCourseWithGrade> viewEnrolledCourses(String studentEmail);
    void enrollForCourse(String studentEmail, String courseCode);
}