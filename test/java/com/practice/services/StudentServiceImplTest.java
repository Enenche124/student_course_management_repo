package com.practice.services;

import com.practice.data.models.Student;
import com.practice.data.repositories.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class StudentServiceImplTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentServiceImpl studentServiceImpl;

    @BeforeEach
    void cleanDb(){
        studentRepository.deleteAll();
    }

     @Test
    public void testThatAddStudentMethodWorks() {
         Student student = new Student();
         student.setName("John Adah");
         student.setEmail("john@gmail.com");
         student.setPassword("john123");
         student.setMatricNumber("57421");


         Student savedStudent = studentServiceImpl.addStudent(student);

         assertNotNull(savedStudent);
         assertNotNull(savedStudent.getId());
         assertEquals("John Adah", savedStudent.getName());
     }
    @Test
    public void testThatFindAllMethodWorks() {
         Student student1 = new Student();
         student1.setName("jerry emma");
         student1.setEmail("john@gmail.com");
         student1.setPassword("john123");
         student1.setMatricNumber("57421");

         Student student2 = new Student();
         student2.setName("John Adah");
         student2.setEmail("john@gmail.com");
         student2.setPassword("john123");
         student2.setMatricNumber("57421");

         studentServiceImpl.addStudent(student1);
         studentServiceImpl.addStudent(student2);

         var foundStudents = studentServiceImpl.findAllStudents();

         assertNotNull(foundStudents);
         assertEquals(2, foundStudents.size());
         assertEquals("jerry emma", foundStudents.get(0).getName());
         assertTrue(foundStudents.stream().anyMatch(student -> student.getId().equals(student1.getId())));
         assertTrue(foundStudents.stream().anyMatch(student -> student.getId().equals(student2.getId())));
         assertTrue(foundStudents.stream().anyMatch(student -> student.getName().equals("John Adah")));
     }

    @Test
    public void testThatFindStudentById_methodWorks(){
         Student student1 = new Student();
         student1.setName("mike fara");
         student1.setEmail("mike@gmail.com");
         student1.setPassword("mike123");
         student1.setMatricNumber("57421");

         Student student2 = new Student();
         student2.setName("nikel baby");
         student2.setEmail("nikel@gmail.com");
         student2.setPassword("nikel123");
         student2.setMatricNumber("57421");

         studentServiceImpl.addStudent(student1);
         studentServiceImpl.addStudent(student2);

         Student foundStudent = studentServiceImpl.findStudentById(student1.getId());
         assertNotNull(foundStudent);
         assertEquals(foundStudent.getId(), student1.getId());
     }

   @Test
    public void testThatDeleteStudentById_methodWorks(){

       Student student1 = new Student();
       student1.setName("niko niko");
       student1.setEmail("niko@gmail.com");
       student1.setPassword("niko123");
       student1.setMatricNumber("57421");

       Student student2 = new Student();
       student2.setName("moses MD");
       student2.setEmail("moses@gmail.com");
       student2.setPassword("moses123");
       student2.setMatricNumber("57421");

       studentServiceImpl.addStudent(student1);
       studentServiceImpl.addStudent(student2);

       var foundStudent = studentServiceImpl.findStudentById(student1.getId());
       studentServiceImpl.deleteStudentById(foundStudent.getId());
       assertEquals(1, studentServiceImpl.count());

   }

   @Test
    public void testThatFindByEmail_methodWorks(){

       Student student1 = new Student();
       student1.setName("majek majek");
       student1.setEmail("majek@gmail.com");
       student1.setPassword("majek123");
       student1.setMatricNumber("57421");

       Student student2 = new Student();
       student2.setName("yemi malis");
       student2.setEmail("yemi@gmail.com");
       student2.setPassword("yemi123");
       student2.setMatricNumber("57421");

       studentServiceImpl.addStudent(student1);
       studentServiceImpl.addStudent(student2);

      studentServiceImpl.findAllStudents();

       Student foundStudent = studentServiceImpl.findStudentByEmail(student1.getEmail());
       assertNotNull(foundStudent);
       assertEquals("majek majek", foundStudent.getName());
   }

   @Test
    public void testThatDeleteStudentByEmail_methodWorks(){
       Student student1 = new Student();
       student1.setName("majek majek");
       student1.setEmail("majek@gmail.com");
       student1.setPassword("majek123");
       student1.setMatricNumber("57421");

       studentServiceImpl.addStudent(student1);
        studentServiceImpl.deleteStudentByEmail(student1.getEmail());

        Student deleted =  studentServiceImpl.findStudentByEmail(student1.getEmail());
        assertNull(deleted);
        assertEquals(0, studentServiceImpl.count());

   }




}