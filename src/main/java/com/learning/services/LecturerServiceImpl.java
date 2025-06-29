package com.learning.services;

import com.learning.data.models.Course;
import com.learning.data.models.Grade;
import com.learning.data.models.Student;
import com.learning.data.repositories.CourseRepository;
import com.learning.data.repositories.GradeRepository;
import com.learning.data.repositories.LecturerRepository;
import com.learning.data.repositories.StudentRepository;
import com.learning.dtos.studentsSumary.StudentSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LecturerServiceImpl implements LecturerService {

    private final LecturerRepository lecturerRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final GradeRepository gradeRepository;

    @Autowired
    public LecturerServiceImpl(LecturerRepository lecturerRepository,
                               StudentRepository studentRepository,
                               CourseRepository courseRepository,
                               GradeRepository gradeRepository) {
        this.lecturerRepository = lecturerRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.gradeRepository = gradeRepository;
    }

    @Override
    public long count() {
        return lecturerRepository.count();
    }



    @Override
    public void assignGradeToStudent(String studentEmail, String courseCode, double score) {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("Score must be between 0 and 100");
        }

        Student student = studentRepository.findStudentByEmail(studentEmail)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with email: " + studentEmail));

        Course course = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with code: " + courseCode));

        if (!student.getEnrolledCourses().contains(course)) {
            throw new IllegalStateException("Student is not enrolled in the course: " + courseCode);
        }

        String grade = mapScoreToGrade(score);

        Grade gradeRecord = gradeRepository.findByStudentEmailAndCourseCode(studentEmail, courseCode)
                .orElse(new Grade());
        gradeRecord.setStudentEmail(studentEmail);
        gradeRecord.setCourseCode(courseCode);
        gradeRecord.setScore(score);
        gradeRecord.setGrade(grade);
        gradeRepository.save(gradeRecord);

        List<Grade> courseGrades = gradeRepository.findAll().stream()
                .filter(g -> g.getCourseCode().equals(courseCode))
                .sorted((g1, g2) -> Double.compare(g2.getScore(), g1.getScore()))
                .toList();

        int position = 1;
        for (int i = 0; i < courseGrades.size(); i++) {
            if (i > 0 && courseGrades.get(i).getScore() < courseGrades.get(i - 1).getScore()) {
                position = i + 1;
            }
            courseGrades.get(i).setPosition(position);
            gradeRepository.save(courseGrades.get(i));
        }
    }


    @Override
    public List<StudentSummary> viewAllEnrolledStudents(String lecturerEmail) {
        List<Course> lecturerCourses = courseRepository.findByCourseInstructorEmail(lecturerEmail);
        List<String> courseCodes = lecturerCourses.stream()
                .map(Course::getCourseCode)
                .toList();

        return studentRepository.findAll().stream()
                .filter(student -> student.getEnrolledCourses().stream()
                        .anyMatch(course -> courseCodes.contains(course.getCourseCode())))
                .map(student -> new StudentSummary(student.getName(), student.getEmail()))
                .collect(Collectors.toList());
    }


    @Override
    public List<Course> viewAllCourses(String instructorEmail) {
        return courseRepository.findByCourseInstructorEmail(instructorEmail);
    }


    private String mapScoreToGrade(double score) {
        if (score >= 70) return "A";
        if (score >= 60) return "B";
        if (score >= 50) return "C";
        if (score >= 45) return "D";
        if (score >= 40) return "E";
        return "F";
    }

}