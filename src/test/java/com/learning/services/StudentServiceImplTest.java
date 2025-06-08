package com.learning.services;

import com.learning.data.models.Course;
import com.learning.data.models.Role;
import com.learning.data.models.Student;
import com.learning.data.repositories.CourseRepository;
import com.learning.data.repositories.StudentRepository;
import com.learning.dtos.EnrolledCourseWithGrade;
import com.learning.dtos.requests.LoginRequest;
import com.learning.dtos.requests.RegisterRequest;
import com.learning.dtos.responses.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class StudentServiceImplTest {

    @Autowired
    private StudentServiceImpl studentService;

    @Autowired
    private AuthenticationServiceImpl authenticationService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private InstructorServiceImpl instructorService;

    @BeforeEach
    public void setUp() {
        studentRepository.deleteAll();
        courseRepository.deleteAll();
    }

    @Test
    public void testThatStudentCanEnrollInCourse() {
        RegisterRequest studentRegisterRequest = new RegisterRequest();
        studentRegisterRequest.setName("Peter Parker");
        studentRegisterRequest.setEmail("john@gmail.com");
        studentRegisterRequest.setPassword("Spidey@123");
        studentRegisterRequest.setRole(Role.STUDENT);
        authenticationService.register(studentRegisterRequest);

        LoginRequest studentLoginRequest = new LoginRequest();
        studentLoginRequest.setEmail(studentRegisterRequest.getEmail());
        studentLoginRequest.setPassword(studentRegisterRequest.getPassword());
        LoginResponse studentLoginResponse = authenticationService.login(studentLoginRequest);
        assertNotNull(studentLoginResponse);

        RegisterRequest instructorRegisterRequest = new RegisterRequest();
        instructorRegisterRequest.setName("Chibuzor Ekeoma");
        instructorRegisterRequest.setRole(Role.INSTRUCTOR);
        instructorRegisterRequest.setPassword("Chibuzor@123");
        instructorRegisterRequest.setEmail("chibuzor@gmail.com");
        authenticationService.register(instructorRegisterRequest);

        LoginRequest instructorLoginRequest = new LoginRequest();
        instructorLoginRequest.setEmail(instructorRegisterRequest.getEmail());
        instructorLoginRequest.setPassword(instructorRegisterRequest.getPassword());
        authenticationService.login(instructorLoginRequest);

        Course course = new Course();
        course.setCourseCode("Math111");
        course.setCourseTitle("Computer Science");
        course.setCourseDescription("Become a scientist in one month");
        Course createdCourse = instructorService.createCourse(course, instructorRegisterRequest.getEmail());
        assertNotNull(createdCourse);

        studentService.enrollForCourse(studentRegisterRequest.getEmail(), createdCourse.getCourseCode());

        Student enrolledStudent = studentRepository.findStudentByEmail(studentRegisterRequest.getEmail()).orElse(null);
        assertNotNull(enrolledStudent);
        assertFalse(enrolledStudent.getEnrolledCourses().isEmpty());
        assertEquals("Math111", enrolledStudent.getEnrolledCourses().get(0).getCourseCode());
    }

    @Test
    public void testThatStudentCanViewAllAvailableCourses() {
        Course course1 = new Course();
        course1.setCourseCode("Math111");
        course1.setCourseDescription("Learn Java early programming");
        course1.setCourseTitle("Java");
        courseRepository.save(course1);

        Course course2 = new Course();
        course2.setCourseCode("Math101");
        course2.setCourseDescription("Learn Python early programming");
        course2.setCourseTitle("Python");
        courseRepository.save(course2);

        List<Course> availableCourses = studentService.viewAllAvailableCourses();
        assertNotNull(availableCourses);
        assertEquals(2, availableCourses.size());
        assertTrue(availableCourses.stream().anyMatch(course -> course.getCourseCode().equals(course1.getCourseCode())));
    }

    @Test
    public void testThatStudentCanViewAllCoursesTheyEnrolledIn() {
        RegisterRequest studentRegisterRequest = new RegisterRequest();
        studentRegisterRequest.setName("Alice");
        studentRegisterRequest.setEmail("alice@gmail.com");
        studentRegisterRequest.setPassword("Alice@123");
        studentRegisterRequest.setRole(Role.STUDENT);
        authenticationService.register(studentRegisterRequest);

        LoginRequest studentLoginRequest = new LoginRequest();
        studentLoginRequest.setPassword(studentRegisterRequest.getPassword());
        studentLoginRequest.setEmail(studentRegisterRequest.getEmail());
        LoginResponse studentLoginResponse = authenticationService.login(studentLoginRequest);
        assertNotNull(studentLoginResponse);

        RegisterRequest instructorRegisterRequest = new RegisterRequest();
        instructorRegisterRequest.setName("Chibuzor Ekeoma");
        instructorRegisterRequest.setRole(Role.INSTRUCTOR);
        instructorRegisterRequest.setPassword("Chibuzor@123");
        instructorRegisterRequest.setEmail("chibuzor@gmail.com");
        authenticationService.register(instructorRegisterRequest);

        LoginRequest instructorLoginRequest = new LoginRequest();
        instructorLoginRequest.setEmail(instructorRegisterRequest.getEmail());
        instructorLoginRequest.setPassword(instructorRegisterRequest.getPassword());
        LoginResponse instructorLoginResponse = authenticationService.login(instructorLoginRequest);
        assertNotNull(instructorLoginResponse);
        assertEquals("Instructor logged in successfully", instructorLoginResponse.getMessage());

        Course course = new Course();
        course.setCourseCode("MTH101");
        course.setCourseTitle("Mathematics 101");
        course.setCourseDescription("Basic Math");
        Course courseTwo = new Course();
        courseTwo.setCourseCode("CH122");
        courseTwo.setCourseTitle("Chemistry 122");
        courseTwo.setCourseDescription("Basic Chemistry");

        Course createdCourseTwo = instructorService.createCourse(courseTwo, instructorRegisterRequest.getEmail());
        Course createdCourse = instructorService.createCourse(course, instructorRegisterRequest.getEmail());
        assertNotNull(createdCourse);
        assertEquals("MTH101", createdCourse.getCourseCode());

        studentService.enrollForCourse(studentRegisterRequest.getEmail(), createdCourse.getCourseCode());
        List<EnrolledCourseWithGrade> coursesEnrolled = studentService.viewEnrolledCourses(studentLoginRequest.getEmail());
        assertNotNull(coursesEnrolled);
        assertEquals(1, coursesEnrolled.size());
        assertEquals("MTH101", coursesEnrolled.get(0).getCourseCode());
    }

    @Test
    public void testDuplicateEnrollmentThrowsException() {
        RegisterRequest studentRegisterRequest = new RegisterRequest();
        studentRegisterRequest.setName("Peter Parker");
        studentRegisterRequest.setEmail("peter@gmail.com");
        studentRegisterRequest.setPassword("Spidey@123");
        studentRegisterRequest.setRole(Role.STUDENT);
        authenticationService.register(studentRegisterRequest);

        RegisterRequest instructorRegisterRequest = new RegisterRequest();
        instructorRegisterRequest.setName("Chibuzor Ekeoma");
        instructorRegisterRequest.setRole(Role.INSTRUCTOR);
        instructorRegisterRequest.setPassword("Chibuzor@123");
        instructorRegisterRequest.setEmail("chibuzor@gmail.com");
        authenticationService.register(instructorRegisterRequest);

        Course course = new Course();
        course.setCourseCode("Math111");
        course.setCourseTitle("Computer Science");
        course.setCourseDescription("Become a scientist in one month");
        Course createdCourse = instructorService.createCourse(course, instructorRegisterRequest.getEmail());

        studentService.enrollForCourse(studentRegisterRequest.getEmail(), createdCourse.getCourseCode());
        assertThrows(IllegalStateException.class, () ->
                studentService.enrollForCourse(studentRegisterRequest.getEmail(), createdCourse.getCourseCode()));
    }

    @Test
    public void testEnrollWithInvalidCourseCodeThrowsException() {
        RegisterRequest studentRegisterRequest = new RegisterRequest();
        studentRegisterRequest.setName("Peter Parker");
        studentRegisterRequest.setEmail("peter@gmail.com");
        studentRegisterRequest.setPassword("Spidey@123");
        studentRegisterRequest.setRole(Role.STUDENT);
        authenticationService.register(studentRegisterRequest);

        assertThrows(IllegalArgumentException.class, () ->
                studentService.enrollForCourse(studentRegisterRequest.getEmail(), "INVALID_CODE"));
    }

    @Test
    public void testViewAllAvailableCoursesReturnsEmptyList() {
        List<Course> courses = studentService.viewAllAvailableCourses();
        assertNotNull(courses);
        assertTrue(courses.isEmpty());
    }

    @Test
    public void testCaseInsensitiveEmailEnrollment() {
        RegisterRequest studentRegisterRequest = new RegisterRequest();
        studentRegisterRequest.setName("Peter Parker");
        studentRegisterRequest.setEmail("Peter@Gmail.com");
        studentRegisterRequest.setPassword("Spidey@123");
        studentRegisterRequest.setRole(Role.STUDENT);
        authenticationService.register(studentRegisterRequest);

        RegisterRequest instructorRegisterRequest = new RegisterRequest();
        instructorRegisterRequest.setName("Chibuzor Ekeoma");
        instructorRegisterRequest.setRole(Role.INSTRUCTOR);
        instructorRegisterRequest.setPassword("Chibuzor@123");
        instructorRegisterRequest.setEmail("chibuzor@gmail.com");
        authenticationService.register(instructorRegisterRequest);

        Course course = new Course();
        course.setCourseCode("Math111");
        course.setCourseTitle("Computer Science");
        course.setCourseDescription("Become a scientist in one month");
        Course createdCourse = instructorService.createCourse(course, instructorRegisterRequest.getEmail());

        studentService.enrollForCourse("peter@gmail.com", createdCourse.getCourseCode());
        Student enrolledStudent = studentRepository.findStudentByEmail("peter@gmail.com").orElse(null);
        assertNotNull(enrolledStudent);
        assertFalse(enrolledStudent.getEnrolledCourses().isEmpty());
    }
}