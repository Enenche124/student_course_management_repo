package com.learning.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@Document(collection = "instructors")
public class Lecturer {
    @Id
    private String id;

    @NotBlank
    private String name;

    private String password;

    private Role role;

    @NotBlank
    @Email
    @Indexed(unique = true, collation = "{ locale: 'en', strength: 2 }")
    private String email;
}