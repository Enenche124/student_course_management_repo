package com.learning.services;

import com.learning.data.models.Course;
import com.learning.data.models.Instructor;
import com.learning.data.models.Student;
import com.learning.data.repositories.CourseRepository;
import com.learning.data.repositories.InstructorRepository;
import com.learning.data.repositories.StudentRepository;
import com.learning.dtos.studentsSumary.StudentSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InstructorServiceImpl implements InstructorService {

    private final InstructorRepository instructorRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public InstructorServiceImpl(InstructorRepository instructorRepository,
                                 StudentRepository studentRepository,
                                 CourseRepository courseRepository) {
        this.instructorRepository = instructorRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public long count() {
        return instructorRepository.count();
    }

    @Override
    public Course createCourse(Course course, String instructorEmail) {
        if (course.getCourseCode() == null || course.getCourseCode().isBlank()) {
            throw new IllegalArgumentException("Course code is required");
        }
        if (course.getCourseTitle() == null || course.getCourseTitle().isBlank()) {
            throw new IllegalArgumentException("Course name is required");
        }

        Optional<Course> existingCourse = courseRepository.findByCourseCode(course.getCourseCode());
        if (existingCourse.isPresent()) {
            throw new IllegalArgumentException("A course with this code already exists");
        }

        Optional<Instructor> optionalInstructor = instructorRepository.findByEmail(instructorEmail);
        if (optionalInstructor.isEmpty()) {
            throw new IllegalArgumentException("Instructor not found");
        }

        course.setCourseInstructorEmail(optionalInstructor.get().getEmail());
        return courseRepository.save(course);
    }

    @Override
    public void assignGradeToStudent(String studentEmail, String courseCode, String grade) {
        if (grade == null || grade.trim().isEmpty()) {
            throw new IllegalArgumentException("Grade cannot be empty");
        }

        Student student = studentRepository.findStudentByEmail(studentEmail)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with email: " + studentEmail));

        Course course = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with code: " + courseCode));

        if (!student.getEnrolledCourses().contains(course)) {
            throw new IllegalStateException("Student is not enrolled in the course: " + courseCode);
        }

        course.assignOrUpdateGrade(studentEmail, grade);
        courseRepository.save(course);
    }

    @Override
    public List<StudentSummary> viewAllEnrolledStudents(String instructorEmail) {
        List<Course> instructorCourses = courseRepository.findByCourseInstructorEmail(instructorEmail);
        return studentRepository.findByEnrolledCoursesIn(instructorCourses)
                .stream()
                .map(student -> new StudentSummary(student.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Course> viewAllCourses(String instructorEmail) {
        return courseRepository.findByCourseInstructorEmail(instructorEmail);
    }
}