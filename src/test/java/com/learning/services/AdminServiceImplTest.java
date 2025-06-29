package com.learning.services;

import com.learning.data.models.Course;
import com.learning.data.models.Role;
import com.learning.data.repositories.CourseRepository;
import com.learning.data.repositories.LecturerRepository;
import com.learning.dtos.requests.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AdminServiceImplTest {

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private LecturerRepository lecturerRepository;

    @Autowired
    private AdminServiceImpl adminServiceImpl;
    @Autowired
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    public void setUp() {
        lecturerRepository.deleteAll();
    }
    @Test
    public void testAdminCanViewAllLecturers() {

        RegisterRequest lecturer1 = new RegisterRequest();
        lecturer1.setName("Dr. Strange");
        lecturer1.setEmail("strange@univ.edu");
        lecturer1.setPassword("Magic@123");
        lecturer1.setRole(Role.LECTURER);
        authenticationService.register(lecturer1);

        RegisterRequest lecturer2 = new RegisterRequest();
        lecturer2.setName("Prof. Xavier");
        lecturer2.setEmail("xavier@univ.edu");
        lecturer2.setPassword("Mind@123");
        lecturer2.setRole(Role.LECTURER);
        authenticationService.register(lecturer2);


        List<String> lecturerEmails = adminServiceImpl.viewAllLecturerEmails();


        assertNotNull(lecturerEmails);
        assertEquals(2, lecturerEmails.size());
        assertTrue(lecturerEmails.contains("strange@univ.edu"));
        assertTrue(lecturerEmails.contains("xavier@univ.edu"));
    }


    @Test
    public void testAdminCanAssignLecturerToCourse_andCannotAssignTwice() {

        RegisterRequest lecturerRequest = new RegisterRequest();
        lecturerRequest.setName("Dr. Banner");
        lecturerRequest.setEmail("banner@univ.edu");
        lecturerRequest.setPassword("Smash@123");
        lecturerRequest.setRole(Role.LECTURER);
        authenticationService.register(lecturerRequest);


        String courseCode = "BIO101";
        adminServiceImpl.createCourse(courseCode, "Biology", "Study of living things");

        adminServiceImpl.assignCourseToLecturer(courseCode, lecturerRequest.getEmail());

        Course course = courseRepository.findByCourseCode(courseCode).orElseThrow();
        assertEquals("banner@univ.edu", course.getCourseInstructorEmail());


        assertThrows(IllegalStateException.class, () ->
                adminServiceImpl.assignCourseToLecturer(courseCode, lecturerRequest.getEmail())
        );
    }

    @Test
    public void testAdminCanCreateNewCourse() {
        String courseCode = "CS101";
        String title = "Intro to Computer Science";
        String description = "Basics of computing";

        adminServiceImpl.createCourse(courseCode, title, description);

        Course created = courseRepository.findByCourseCode(courseCode).orElseThrow();
        assertEquals(courseCode, created.getCourseCode());
        assertEquals(title, created.getCourseTitle());
        assertEquals(description, created.getCourseDescription());
    }

    @Test
    public void testThatAdminCan_deleteLecturer() {
        RegisterRequest lecturerRequest = new RegisterRequest();
        lecturerRequest.setName("Dr. Banner");
        lecturerRequest.setEmail("banner@univ.edu");
        lecturerRequest.setPassword("Smash@123");
        lecturerRequest.setRole(Role.LECTURER);
        authenticationService.register(lecturerRequest);

        adminServiceImpl.deleteLecturerByEmail(lecturerRequest.getEmail());

        assertFalse(lecturerRepository.findByEmail(lecturerRequest.getEmail()).isPresent());

    }

}