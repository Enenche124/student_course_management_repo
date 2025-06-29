package com.learning.services;

import com.learning.data.models.Course;

import java.util.List;

public interface AdminService {

    Course createCourse(String courseCode, String title, String description);

    void assignCourseToLecturer(String courseCode, String lecturerEmail);
    List<String> viewAllLecturerEmails();
    List<Course> viewAllCourses();
    void deleteLecturerByEmail(String email);
    void deleteCourseByCourseCode(String courseCode);

}
