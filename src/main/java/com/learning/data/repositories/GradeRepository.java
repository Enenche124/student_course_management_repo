package com.learning.data.repositories;

import com.learning.data.models.Grade;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface GradeRepository extends MongoRepository<Grade, String> {
    Optional<Grade> findByStudentEmailAndCourseCode(String studentEmail, String courseCode);
}