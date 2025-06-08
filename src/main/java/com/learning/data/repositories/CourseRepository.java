package com.learning.data.repositories;

import com.learning.data.models.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {
    List<Course> findByCourseInstructorEmail(String courseInstructorEmail);
    List<Course> findByCourseInstructorEmailIgnoreCase(String courseInstructorEmail);
    Optional<Course> findByCourseCode(String courseCode);
}