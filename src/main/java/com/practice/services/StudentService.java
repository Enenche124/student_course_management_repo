package com.practice.services;

import com.practice.data.models.Student;

import java.util.List;

public interface StudentService {
    Student addStudent(Student student);
    long count();
    List<Student> findAllStudents();
    void deleteStudentById(String id);
    Student findStudentById(String id);
    Student findStudentByEmail(String email);
}
