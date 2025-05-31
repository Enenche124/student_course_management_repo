package com.practice.services;

import com.practice.data.models.Student;
import com.practice.data.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {


    private final StudentRepository studentRepository;
    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }


    @Override
    public Student addStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public long count() {
        return studentRepository.count();
    }


    @Override
    public List<Student> findAllStudents() {
        return studentRepository.findAll();
    }



    @Override
    public void deleteStudentById(String id) {
        studentRepository.deleteById(id);
    }

    @Override
    public Student findStudentById(String id) {
        return studentRepository.findById(id).orElse(null);
    }

    @Override
    public Student findStudentByEmail(String email) {
        return studentRepository.findStudentByEmail(email).orElse(null);
    }

    public void deleteStudentByEmail(String email) {
        studentRepository.deleteStudentByEmail(email);
    }

}
