package com.learning.services;

import com.learning.data.models.Course;
import com.learning.data.models.Student;
import com.learning.data.repositories.CourseRepository;
import com.learning.data.repositories.StudentRepository;
import com.learning.dtos.EnrolledCourseWithGrade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository, CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public void enrollForCourse(String studentEmail, String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be empty");
        }

        Optional<Student> optionalStudent = studentRepository.findStudentByEmail(studentEmail);
        Optional<Course> optionalCourse = courseRepository.findByCourseCode(courseCode);

        if (optionalStudent.isEmpty()) {
            throw new IllegalArgumentException("Student does not exist");
        }
        if (optionalCourse.isEmpty()) {
            throw new IllegalArgumentException("Course does not exist");
        }

        Student student = optionalStudent.get();
        Course course = optionalCourse.get();

        if (student.getEnrolledCourses().contains(course)) {
            throw new IllegalStateException("Student is already enrolled in course: " + courseCode);
        }

        student.getEnrolledCourses().add(course);
        studentRepository.save(student);
    }

    @Override
    public long count() {
        return studentRepository.count();
    }

    @Override
    public List<Course> viewAllAvailableCourses() {
        return courseRepository.findAll();
    }

    @Override
    public List<EnrolledCourseWithGrade> viewEnrolledCourses(String studentEmail) {
        Optional<Student> optionalStudent = studentRepository.findStudentByEmail(studentEmail);
        if (optionalStudent.isEmpty()) {
            throw new IllegalArgumentException("Student not found");
        }
        Student student = optionalStudent.get();
        return student.getEnrolledCourses().stream()
                .map(course -> {
                    String grade = course.getGrades().getOrDefault(studentEmail, "N/A");
                    return new EnrolledCourseWithGrade(
                            course.getCourseCode(),
                            course.getCourseTitle(),
                            course.getCourseDescription(),
                            grade,
                            course.getCourseInstructorEmail() // Instructor email
                    );
                })
                .collect(Collectors.toList());
    }
}