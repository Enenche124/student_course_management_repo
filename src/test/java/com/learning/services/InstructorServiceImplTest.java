package com.learning.services;

import com.learning.data.models.Course;
import com.learning.data.models.Role;
import com.learning.data.repositories.CourseRepository;
import com.learning.data.repositories.InstructorRepository;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class InstructorServiceImplTest {

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private InstructorServiceImpl instructorServiceImpl;

    @Autowired
    private AuthenticationServiceImpl authenticationService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentServiceImpl studentServiceImpl;

    @BeforeEach
    public void setUp() {
        instructorRepository.deleteAll();
        courseRepository.deleteAll();
        studentRepository.deleteAll();
    }

    @Test
    public void testThatCreateCourseMethodWorks() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("email@124.com");
        registerRequest.setPassword("Password#444");
        registerRequest.setName("Instructor John");
        registerRequest.setRole(Role.INSTRUCTOR);

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

        Course createdCourse = instructorServiceImpl.createCourse(course, registerRequest.getEmail());
        assertNotNull(createdCourse);
        assertEquals(course.getCourseCode(), createdCourse.getCourseCode());
    }

    @Test
    public void testCreateCourseThrowsExceptionIfNoInstructorFound() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("John Adah");
        registerRequest.setRole(Role.INSTRUCTOR);
        registerRequest.setPassword("Password@123");
        registerRequest.setEmail("johnadah@gmail.com");

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
        course.setCourseDescription("Learn Python in two days");
        course.setCourseTitle("Python");
        course.setCourseCode("CS111");

        assertThrows(IllegalArgumentException.class, () ->
                instructorServiceImpl.createCourse(course, "jerry@gmail.com"));
    }

    @Test
    public void testThatInstructorCanViewAllCreatedCourses() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Mike Faraday");
        registerRequest.setRole(Role.INSTRUCTOR);
        registerRequest.setPassword("Faraday@123");
        registerRequest.setEmail("faraday@gmail.com");

        RegisterResponse response = authenticationService.register(registerRequest);
        assertNotNull(response);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword(registerRequest.getPassword());
        loginRequest.setEmail(registerRequest.getEmail());
        LoginResponse loginResponse = authenticationService.login(loginRequest);
        assertNotNull(loginResponse);
        assertEquals("Instructor logged in successfully", loginResponse.getMessage());

        Course courseOne = new Course();
        courseOne.setCourseDescription("Learn Python in two days");
        courseOne.setCourseTitle("Python");
        courseOne.setCourseCode("CS111");

        Course courseTwo = new Course();
        courseTwo.setCourseDescription("Learn Python in two days");
        courseTwo.setCourseTitle("Python");
        courseTwo.setCourseCode("CS101");

        Course createdCourseOne = instructorServiceImpl.createCourse(courseOne, registerRequest.getEmail());
        Course createdCourseTwo = instructorServiceImpl.createCourse(courseTwo, registerRequest.getEmail());
        assertNotNull(createdCourseOne);
        assertNotNull(createdCourseTwo);

        List<Course> courses = instructorServiceImpl.viewAllCourses(registerRequest.getEmail());
        assertNotNull(courses);
        assertEquals(2, courses.size());
    }

    @Test
    public void testInstructorCanViewAllEnrolledStudentsInCoursesCreatedByHim() {
        RegisterRequest instructorRegisterRequest = new RegisterRequest();
        instructorRegisterRequest.setName("Moses Idowu");
        instructorRegisterRequest.setRole(Role.INSTRUCTOR);
        instructorRegisterRequest.setPassword("Moses@123");
        instructorRegisterRequest.setEmail("moses@gmail.com");

        RegisterResponse response = authenticationService.register(instructorRegisterRequest);
        assertNotNull(response);

        LoginRequest instructorLoginRequest = new LoginRequest();
        instructorLoginRequest.setPassword(instructorRegisterRequest.getPassword());
        instructorLoginRequest.setEmail(instructorRegisterRequest.getEmail());
        LoginResponse loginResponse = authenticationService.login(instructorLoginRequest);
        assertNotNull(loginResponse);

        Course courseOne = new Course();
        courseOne.setCourseDescription("Learn Python in two days");
        courseOne.setCourseTitle("Python");
        courseOne.setCourseCode("CS111");

        Course courseTwo = new Course();
        courseTwo.setCourseDescription("Learn advanced Python");
        courseTwo.setCourseTitle("Python");
        courseTwo.setCourseCode("CS101");

        Course createdCourseOne = instructorServiceImpl.createCourse(courseOne, instructorRegisterRequest.getEmail());
        Course createdCourseTwo = instructorServiceImpl.createCourse(courseTwo, instructorRegisterRequest.getEmail());

        RegisterRequest studentOneRegisterRequest = new RegisterRequest();
        studentOneRegisterRequest.setName("Jane Doe");
        studentOneRegisterRequest.setEmail("jane@student.com");
        studentOneRegisterRequest.setPassword("JaneDoe@123");
        studentOneRegisterRequest.setRole(Role.STUDENT);

        RegisterResponse studentRegResponse = authenticationService.register(studentOneRegisterRequest);
        assertNotNull(studentRegResponse);

        LoginRequest studentOneLoginRequest = new LoginRequest();
        studentOneLoginRequest.setPassword(studentOneRegisterRequest.getPassword());
        studentOneLoginRequest.setEmail(studentOneRegisterRequest.getEmail());
        authenticationService.login(studentOneLoginRequest);

        RegisterRequest studentTwoReg = new RegisterRequest();
        studentTwoReg.setName("Eletu Usman");
        studentTwoReg.setEmail("eletu@student.com");
        studentTwoReg.setPassword("Eletuusman@123");
        studentTwoReg.setRole(Role.STUDENT);

        authenticationService.register(studentTwoReg);

        LoginRequest studentTwoLoginRequest = new LoginRequest();
        studentTwoLoginRequest.setEmail(studentTwoReg.getEmail());
        studentTwoLoginRequest.setPassword(studentTwoReg.getPassword());
        authenticationService.login(studentTwoLoginRequest);

        studentServiceImpl.enrollForCourse(studentOneRegisterRequest.getEmail(), createdCourseOne.getCourseCode());
        studentServiceImpl.enrollForCourse(studentTwoLoginRequest.getEmail(), createdCourseTwo.getCourseCode());

        List<StudentSummary> enrolledStudents = instructorServiceImpl.viewAllEnrolledStudents(instructorRegisterRequest.getEmail());
        assertNotNull(enrolledStudents);
        assertEquals(2, enrolledStudents.size());
    }

    @Test
    public void testInstructorAssignsGradeToEnrolledStudent() {
        RegisterRequest instructorRequest = new RegisterRequest();
        instructorRequest.setName("Dr. Strange");
        instructorRequest.setEmail("strange@univ.edu");
        instructorRequest.setPassword("Magic@123");
        instructorRequest.setRole(Role.INSTRUCTOR);

        RegisterResponse instructorResponse = authenticationService.register(instructorRequest);
        assertNotNull(instructorResponse);

        RegisterRequest studentRequest = new RegisterRequest();
        studentRequest.setName("Peter Parker");
        studentRequest.setEmail("peter@univ.edu");
        studentRequest.setPassword("Spidey@123");
        studentRequest.setRole(Role.STUDENT);

        RegisterResponse studentResponse = authenticationService.register(studentRequest);
        assertNotNull(studentResponse);

        Course course = new Course();
        course.setCourseCode("WEB101");
        course.setCourseTitle("Web Development");
        course.setCourseDescription("Learn to sling code like webs.");

        Course createdCourse = instructorServiceImpl.createCourse(course, instructorRequest.getEmail());
        assertNotNull(createdCourse);
        assertEquals("WEB101", createdCourse.getCourseCode());

        studentServiceImpl.enrollForCourse(studentRequest.getEmail(), "WEB101");

        instructorServiceImpl.assignGradeToStudent(studentRequest.getEmail(), "WEB101", "A");

        Course courseWithGrades = courseRepository.findByCourseCode("WEB101").orElseThrow();
        Map<String, String> grades = courseWithGrades.getGrades();
        assertEquals(1, grades.size());
//        GradeEntry gradeEntry = grades.get(0);
//        assertEquals(studentRequest.getEmail(), gradeEntry.getEmail());
//        assertEquals("A", gradeEntry.getGrade());
    }

    @Test
    public void testDuplicateCourseCreationThrowsException() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("instructor@example.com");
        registerRequest.setPassword("Password@123");
        registerRequest.setName("Instructor John");
        registerRequest.setRole(Role.INSTRUCTOR);
        authenticationService.register(registerRequest);

        Course course = new Course();
        course.setCourseCode("CS101");
        course.setCourseTitle("Java");
        course.setCourseDescription("Learn Java");

        instructorServiceImpl.createCourse(course, registerRequest.getEmail());
        assertThrows(IllegalArgumentException.class, () ->
                instructorServiceImpl.createCourse(course, registerRequest.getEmail()));
    }

    @Test
    public void testAssignGradeToNonEnrolledStudentThrowsException() {
        RegisterRequest instructorRequest = new RegisterRequest();
        instructorRequest.setEmail("instructor@example.com");
        instructorRequest.setPassword("Password@123");
        instructorRequest.setName("Dr. Strange");
        instructorRequest.setRole(Role.INSTRUCTOR);
        authenticationService.register(instructorRequest);

        RegisterRequest studentRequest = new RegisterRequest();
        studentRequest.setEmail("student@example.com");
        studentRequest.setPassword("Spidey@123");
        studentRequest.setName("Peter Parker");
        studentRequest.setRole(Role.STUDENT);
        authenticationService.register(studentRequest);

        Course course = new Course();
        course.setCourseCode("WEB101");
        course.setCourseTitle("Web Development");
        course.setCourseDescription("Learn web dev");
        instructorServiceImpl.createCourse(course, instructorRequest.getEmail());

        assertThrows(IllegalStateException.class, () ->
                instructorServiceImpl.assignGradeToStudent(studentRequest.getEmail(), "WEB101", "A"));
    }

    @Test
    public void testViewAllCoursesReturnsEmptyList() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("instructor@example.com");
        registerRequest.setPassword("Password@123");
        registerRequest.setName("Instructor John");
        registerRequest.setRole(Role.INSTRUCTOR);
        authenticationService.register(registerRequest);

        List<Course> courses = instructorServiceImpl.viewAllCourses(registerRequest.getEmail());
        assertNotNull(courses);
        assertTrue(courses.isEmpty());
    }

    @Test
    public void testCaseInsensitiveEmailCourseCreation() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("Instructor@Example.com");
        registerRequest.setPassword("Password@123");
        registerRequest.setName("Instructor John");
        registerRequest.setRole(Role.INSTRUCTOR);
        authenticationService.register(registerRequest);

        Course course = new Course();
        course.setCourseCode("CS101");
        course.setCourseTitle("Java");
        course.setCourseDescription("Learn Java");

        Course createdCourse = instructorServiceImpl.createCourse(course, "instructor@example.com");
        assertNotNull(createdCourse);
        assertEquals("CS101", createdCourse.getCourseCode());
    }
}