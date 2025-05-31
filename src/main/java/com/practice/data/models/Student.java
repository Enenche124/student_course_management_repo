package com.practice.data.models;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document(collection = "students")
public class Student {
    @Id
    private String id;
    private String matricNumber;
    private String name;
    private String password;
    private String role;
    private String email;


}
