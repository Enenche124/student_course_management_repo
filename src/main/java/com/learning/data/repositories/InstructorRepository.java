package com.learning.data.repositories;

import com.learning.data.models.Instructor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstructorRepository extends MongoRepository<Instructor, String> {
    Optional<Instructor> findByEmail(String email);
    void deleteByEmail(String email);
}