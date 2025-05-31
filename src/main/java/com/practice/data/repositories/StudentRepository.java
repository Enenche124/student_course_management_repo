package com.practice.data.repositories;

import com.practice.data.models.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends MongoRepository<Student,String> {
    Optional<Student> findStudentByEmail(String email);
    void deleteStudentByEmail(String email);
}
