package com.learning.data.repositories;

import com.learning.data.models.Admin;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AdminRepository extends MongoRepository<Admin, String> {
    Admin findByEmail(String email);
}
