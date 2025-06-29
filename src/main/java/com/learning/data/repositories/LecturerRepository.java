package com.learning.data.repositories;

import com.learning.data.models.Lecturer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LecturerRepository extends MongoRepository<Lecturer, String> {
    Optional<Lecturer> findByEmail(String email);
    void deleteByEmail(String email);
}