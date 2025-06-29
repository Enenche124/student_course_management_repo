package com.learning.services;

import com.learning.data.models.Course;
import com.learning.data.models.Grade;
import com.learning.data.models.Role;
import com.learning.data.repositories.CourseRepository;
import com.learning.data.repositories.GradeRepository;
import com.learning.data.repositories.LecturerRepository;
import com.learning.data.repositories.StudentRepository;
import com.learning.dtos.requests.LoginRequest;
import com.learning.dtos.requests.RegisterRequest;
import com.learning.dtos.responses.LoginResponse;
import com.learning.dtos.responses.RegisterResponse;
import com.learning.dtos.studentsSumary.StudentSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LecturerServiceImplTest {

    @Autowired
    private LecturerRepository lecturerRepository;

    @Autowired
    private LecturerServiceImpl lecturerServiceImpl;

    @Autowired
    private AuthenticationServiceImpl authenticationService;

    @Autowired
    private AdminServiceImpl adminService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private StudentServiceImpl studentServiceImpl;

    @BeforeEach
    public void setUp() {
        lecturerRepository.deleteAll();
        courseRepository.deleteAll();
        studentRepository.deleteAll();
        gradeRepository.deleteAll();
    }

    @Test
    public void testThatCreateCourseMethodWorks() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("email@124.com");
        registerRequest.setPassword("Password#444");
        registerRequest.setName("Instructor John");
        registerRequest.setRole(Role.LECTURER);

        RegisterResponse response = authenticationService.register(registerRequest);
        assertNotNull(response);
        assertEquals("Instructor registered successfully", response.getMessage());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword(registerRequest.getPassword());
        loginRequest.setEmail(registerRequest.getEmail());
        LoginResponse loginResponse = authenticationService.login(loginRequest);
        assertNotNull(loginResponse);
        assertEquals("Instructor logged in successfully", loginResponse.getMessage());

        Course course = new Course();
        course.setCourseCode("CS101");
        course.setCourseTitle("Java");
        course.setCourseDescription("Learn Java in two months");

        Course createdCourse = adminService.createCourse("CHEM101", "maths", "learn maths");
        assertNotNull(createdCourse);
        assertEquals("CHEM101", createdCourse.getCourseCode());
    }


    @Test
    public void testLecturerCanViewOnlyAssignedCourses() {
        RegisterRequest lecturerRequest = new RegisterRequest();
        lecturerRequest.setName("Dr. Palmer");
        lecturerRequest.setEmail("palmer@univ.edu");
        lecturerRequest.setPassword("Heart@123");
        lecturerRequest.setRole(Role.LECTURER);
        authenticationService.register(lecturerRequest);

        adminService.createCourse("CARD101", "Cardiology I", "Basics of Cardio");
        adminService.createCourse("NEURO101", "Neurology I", "Nervous system basics");
        adminService.createCourse("UNASSIGNED", "Floating Course", "No lecturer yet");

        adminService.assignCourseToLecturer("CARD101", lecturerRequest.getEmail());
        adminService.assignCourseToLecturer("NEURO101", lecturerRequest.getEmail());

        List<Course> lecturerCourses = lecturerServiceImpl.viewAllCourses(lecturerRequest.getEmail());

        assertEquals(2, lecturerCourses.size());

        List<String> courseCodes = lecturerCourses.stream()
                .map(Course::getCourseCode)
                .toList();

        assertTrue(courseCodes.contains("CARD101"));
        assertTrue(courseCodes.contains("NEURO101"));
        assertFalse(courseCodes.contains("UNASSIGNED"));
    }


    @Test
    public void testLecturerCanViewEnrolledStudents() {
        RegisterRequest lecturerRequest = new RegisterRequest();
        lecturerRequest.setName("Dr. Strange");
        lecturerRequest.setEmail("strange@univ.edu");
        lecturerRequest.setPassword("Magic@123");
        lecturerRequest.setRole(Role.LECTURER);
        authenticationService.register(lecturerRequest);

        String courseCode = "MAG101";
        adminService.createCourse(courseCode, "Magic Science", "Advanced mystical arts");
        adminService.assignCourseToLecturer(courseCode, lecturerRequest.getEmail());

        String[] studentEmails = {"wanda@univ.edu", "america@univ.edu"};
        for (String email : studentEmails) {
            RegisterRequest studentReq = new RegisterRequest();
            studentReq.setName("Student " + email);
            studentReq.setEmail(email);
            studentReq.setPassword("Pass@123");
            studentReq.setRole(Role.STUDENT);
            authenticationService.register(studentReq);
            studentServiceImpl.enrollForCourse(email, courseCode);
        }

        List<StudentSummary> summaries = lecturerServiceImpl.viewAllEnrolledStudents(lecturerRequest.getEmail());

        assertEquals(2, summaries.size());
        List<String> names = summaries.stream().map(StudentSummary::getName).toList();
        assertTrue(names.contains("Student wanda@univ.edu"));
        assertTrue(names.contains("Student america@univ.edu"));
    }

    @Test
    public void testLecturerAssignsGradeToEnrolledStudent() {
        RegisterRequest lecturerRequest = new RegisterRequest();
        lecturerRequest.setName("Dr. Strange");
        lecturerRequest.setEmail("strange@univ.edu");
        lecturerRequest.setPassword("Magic@123");
        lecturerRequest.setRole(Role.LECTURER);

        RegisterResponse lecturerResponse = authenticationService.register(lecturerRequest);
        assertNotNull(lecturerResponse);

        RegisterRequest studentRequest = new RegisterRequest();
        studentRequest.setName("Peter Parker");
        studentRequest.setEmail("peter@univ.edu");
        studentRequest.setPassword("Spidey@123");
        studentRequest.setRole(Role.STUDENT);

        RegisterResponse studentResponse = authenticationService.register(studentRequest);
        assertNotNull(studentResponse);

        Course createdCourse = adminService.createCourse("Math121", "maths", "learn maths");
        assertNotNull(createdCourse);
        assertEquals("Math121", createdCourse.getCourseCode());


        studentServiceImpl.enrollForCourse(studentRequest.getEmail(), "Math121");


        lecturerServiceImpl.assignGradeToStudent(studentRequest.getEmail(), "Math121", 88.0);


        Grade grade = gradeRepository.findByStudentEmailAndCourseCode(studentRequest.getEmail(), "Math121")
                .orElseThrow(() -> new AssertionError("Grade not found"));

        assertEquals(88.0, grade.getScore(), 0.01);
        assertEquals("A", grade.getGrade());
        assertEquals(1, grade.getPosition());
    }



    @Test
    public void testAssignGradeToNonEnrolledStudentThrowsException() {
        RegisterRequest lecturerRequest = new RegisterRequest();
        lecturerRequest.setEmail("instructor@example.com");
        lecturerRequest.setPassword("Password@123");
        lecturerRequest.setName("Dr. Strange");
        lecturerRequest.setRole(Role.LECTURER);
        authenticationService.register(lecturerRequest);

        RegisterRequest studentRequest = new RegisterRequest();
        studentRequest.setEmail("student@example.com");
        studentRequest.setPassword("Spidey@123");
        studentRequest.setName("Peter Parker");
        studentRequest.setRole(Role.STUDENT);
        authenticationService.register(studentRequest);


        adminService.createCourse("Math11", "maths", "learn maths");

        assertThrows(IllegalStateException.class, () ->
                lecturerServiceImpl.assignGradeToStudent(studentRequest.getEmail(), "Math11", 85.0)); // changed from "A" to score
    }


    @Test
    public void testViewAllCoursesReturnsEmptyList() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("instructor@example.com");
        registerRequest.setPassword("Password@123");
        registerRequest.setName("Instructor John");
        registerRequest.setRole(Role.LECTURER);
        authenticationService.register(registerRequest);

        List<Course> courses = lecturerServiceImpl.viewAllCourses(registerRequest.getEmail());
        assertNotNull(courses);
        assertTrue(courses.isEmpty());
    }

    @Test
    public void testCaseInsensitiveEmailCourseCreation() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("Instructor@Example.com");
        registerRequest.setPassword("Password@123");
        registerRequest.setName("Instructor John");
        registerRequest.setRole(Role.LECTURER);
        authenticationService.register(registerRequest);

        Course createdCourse = adminService.createCourse("Math101", "maths", "learn maths");
        assertNotNull(createdCourse);
        assertEquals("Math101", createdCourse.getCourseCode());
    }


    @Test
    public void testStudentRankingBasedOnScore() {

        RegisterRequest lecturerRequest = new RegisterRequest();
        lecturerRequest.setName("Dr. Banner");
        lecturerRequest.setEmail("banner@univ.edu");
        lecturerRequest.setPassword("Gamma@123");
        lecturerRequest.setRole(Role.LECTURER);
        authenticationService.register(lecturerRequest);


        adminService.createCourse("Math1011", "SCI101", "learn maths");


        String[] emails = {"bruce@univ.edu", "natasha@univ.edu", "tony@univ.edu"};
        double[] scores = {75.0, 60.0, 90.0};

        for (int i = 0; i < emails.length; i++) {
            RegisterRequest studentRequest = new RegisterRequest();
            studentRequest.setName("Student " + i);
            studentRequest.setEmail(emails[i]);
            studentRequest.setPassword("Test@123");
            studentRequest.setRole(Role.STUDENT);
            authenticationService.register(studentRequest);

            studentServiceImpl.enrollForCourse(emails[i], "Math1011");

            lecturerServiceImpl.assignGradeToStudent(emails[i], "Math1011", scores[i]);
        }

        Grade gradeTony = gradeRepository.findByStudentEmailAndCourseCode("tony@univ.edu", "Math1011").orElseThrow();
        Grade gradeBruce = gradeRepository.findByStudentEmailAndCourseCode("bruce@univ.edu", "Math1011").orElseThrow();
        Grade gradeNatasha = gradeRepository.findByStudentEmailAndCourseCode("natasha@univ.edu", "Math1011").orElseThrow();

        assertEquals(1, gradeTony.getPosition());
        assertEquals(2, gradeBruce.getPosition());
        assertEquals(3, gradeNatasha.getPosition());
        assertEquals("A", gradeTony.getGrade());
        assertEquals("A", gradeBruce.getGrade());
        assertEquals("B", gradeNatasha.getGrade());
    }

}