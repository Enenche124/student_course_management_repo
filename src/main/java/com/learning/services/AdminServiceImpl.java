package com.learning.services;

import com.learning.data.models.Admin;
import com.learning.data.models.Course;
import com.learning.data.models.Lecturer;
import com.learning.data.repositories.CourseRepository;
import com.learning.data.repositories.LecturerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final CourseRepository courseRepository;
    private final LecturerRepository lecturerRepository;
    private AdminServiceImpl adminServiceImpl;

    @Autowired
    public AdminServiceImpl(CourseRepository courseRepository,
                            LecturerRepository lecturerRepository) {
        this.courseRepository = courseRepository;
        this.lecturerRepository = lecturerRepository;
    }

    @Override
    public Course createCourse(String courseCode, String title, String description) {
        if (courseCode == null || courseCode.isBlank() ||
                title == null || title.isBlank()) {
            throw new IllegalArgumentException("Course code and title are required");
        }

        if (courseRepository.findByCourseCode(courseCode).isPresent()) {
            throw new IllegalArgumentException("Course with this code already exists");
        }

        Course course = new Course();
        course.setCourseCode(courseCode);
        course.setCourseTitle(title);
        course.setCourseDescription(description);

        courseRepository.save(course);
        return course;
    }

    @Override
    public void assignCourseToLecturer(String courseCode, String lecturerEmail) {
        Course course = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        Lecturer lecturer = lecturerRepository.findByEmail(lecturerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Lecturer not found"));

        if (lecturerEmail.equalsIgnoreCase(course.getCourseInstructorEmail())) {
            throw new IllegalStateException("This course is already assigned to this lecturer");
        }

        course.setCourseInstructorEmail(lecturer.getEmail());
        courseRepository.save(course);
    }

    @Override
    public List<String> viewAllLecturerEmails() {
        return lecturerRepository.findAll()
                .stream()
                .map(Lecturer::getEmail)
                .toList();
    }

    @Override
    public List<Course> viewAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public void deleteLecturerByEmail(String email) {
        Lecturer lecturer = lecturerRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Lecturer not found"));
        lecturerRepository.delete(lecturer);
    }

    @Override
    public void deleteCourseByCourseCode(String courseCode) {
        Course course = courseRepository.findByCourseCode(courseCode).orElseThrow(() -> new IllegalArgumentException("Course not found"));
        courseRepository.delete(course);
    }


}
