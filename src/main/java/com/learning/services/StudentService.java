package com.learning.services;

import com.learning.data.models.Course;
import com.learning.data.models.Student;
import com.learning.dtos.EnrolledCourseWithGrade;
import com.learning.dtos.studentsSumary.StudentPerformanceDto;

import java.util.List;
import java.util.Optional;

public interface StudentService {
    long count();
    List<Course> viewAllAvailableCourses();
    List<EnrolledCourseWithGrade> viewEnrolledCourses(String studentEmail);
    void enrollForCourse(String studentEmail, String courseCode);
    List<StudentPerformanceDto> getStudentPerformance(String studentEmail);

    Optional<Student> findStudentByEmail(String email);
}