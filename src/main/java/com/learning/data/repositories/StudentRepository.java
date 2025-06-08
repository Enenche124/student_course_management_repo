package com.learning.data.repositories;

import com.learning.data.models.Course;
import com.learning.data.models.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {
    Optional<Student> findStudentByEmail(String email);
    List<Student> findByEnrolledCoursesIn(List<Course> courses);
}