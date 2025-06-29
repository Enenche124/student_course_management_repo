package com.learning.services;

import com.learning.data.models.Course;
import com.learning.data.models.Grade;
import com.learning.data.models.Student;
import com.learning.data.repositories.CourseRepository;
import com.learning.data.repositories.GradeRepository;
import com.learning.data.repositories.StudentRepository;
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

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final GradeRepository gradeRepository;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository,
                              CourseRepository courseRepository,
                              GradeRepository gradeRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.gradeRepository = gradeRepository;
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
                    String grade = gradeRepository.findByStudentEmailAndCourseCode(studentEmail, course.getCourseCode())
                            .map(Grade::getGrade)
                            .orElse("N/A");
                    return new EnrolledCourseWithGrade(
                            course.getCourseCode(),
                            course.getCourseTitle(),
                            course.getCourseDescription(),
                            grade,
                            course.getCourseInstructorEmail()
                    );
                })
                .collect(Collectors.toList());
    }


    @Override
    public List<StudentPerformanceDto> getStudentPerformance(String studentEmail) {
        Student student = studentRepository.findStudentByEmail(studentEmail)
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
    public Optional<Student> findStudentByEmail(String email) {
        return studentRepository.findStudentByEmail(email);
    }

}