package com.learning.services;

import com.learning.data.models.Course;
import com.learning.data.models.Grade;
import com.learning.data.models.User;
import com.learning.data.repositories.CourseRepository;
import com.learning.data.repositories.GradeRepository;
import com.learning.data.repositories.UserRepository;
import com.learning.dtos.EnrolledCourseWithGrade;
import com.learning.dtos.studentsSumary.StudentPerformanceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {


    private final CourseRepository courseRepository;
    private final GradeRepository gradeRepository;
    private final UserRepository userRepository;

    @Autowired
    public StudentServiceImpl(
                              CourseRepository courseRepository,
                              GradeRepository gradeRepository, UserRepository userRepository) {

        this.courseRepository = courseRepository;
        this.gradeRepository = gradeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void enrollForCourse(String studentEmail, String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be empty");
        }


        Optional<User> optionalStudent = userRepository.findByEmail(studentEmail);
        Optional<Course> optionalCourse = courseRepository.findByCourseCode(courseCode);

        if (optionalStudent.isEmpty()) {
            throw new IllegalArgumentException("Student does not exist");
        }
        if (optionalCourse.isEmpty()) {
            throw new IllegalArgumentException("Course does not exist");
        }

        User student = optionalStudent.get();
        Course course = optionalCourse.get();

        if (student.getEnrolledCourses().contains(course)) {
            throw new IllegalStateException("Student is already enrolled in course: " + courseCode);
        }

        student.getEnrolledCourses().add(course);
        userRepository.save(student);
    }

    @Override
    public long count() {
        return userRepository.count();
    }

    @Override
    public List<Course> viewAllAvailableCourses() {
        return courseRepository.findAll();
    }

    @Override
    public List<EnrolledCourseWithGrade> viewEnrolledCourses(String studentEmail) {
        Optional<User> optionalStudent = userRepository.findByEmail(studentEmail);
        if (optionalStudent.isEmpty()) {
            throw new IllegalArgumentException("Student not found");
        }
        User student = optionalStudent.get();
        return student.getEnrolledCourses().stream()
                .map(course -> {
                    String grade = gradeRepository.findByStudentEmailAndCourseCode(studentEmail, course.getCourseCode())
                            .map(Grade::getGrade)
                            .orElse("N/A");
                    return new EnrolledCourseWithGrade(
                            course.getCourseCode(),
                            course.getCourseTitle(),
                            course.getCourseDescription(),
                            grade,
                            course.getCourseLecturerEmail()
                    );
                })
                .collect(Collectors.toList());
    }


    @Override
    public List<StudentPerformanceDto> getStudentPerformance(String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        List<StudentPerformanceDto> performanceList = new ArrayList<>();

        for (Course course : student.getEnrolledCourses()) {
            Optional<Grade> gradeOpt = gradeRepository.findByStudentEmailAndCourseCode(studentEmail, course.getCourseCode());

            if (gradeOpt.isPresent()) {
                Grade grade = gradeOpt.get();
                StudentPerformanceDto performance = new StudentPerformanceDto(
                        course.getCourseCode(),
                        course.getCourseTitle(),
                        grade.getScore(),
                        grade.getGrade(),
                        grade.getPosition()
                );
                performanceList.add(performance);
            }
        }

        return performanceList;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}