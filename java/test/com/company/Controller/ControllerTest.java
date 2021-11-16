package com.company.Controller;

import com.company.Exceptions.*;
import com.company.Model.Course;
import com.company.Model.Student;
import com.company.Model.Teacher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class test the most important methods of Controller Class {@link Controller}
 */
class ControllerTest {
    private Controller controller;
    private Student student;
    private Student student1;
    private Teacher teacher;
    private Teacher teacher1;
    private Course course;


    @BeforeEach
    void setUp() throws IOException {
        this.controller=new Controller();
        this.student=new Student("Aleman","Mihnea");
        this.student1=new Student("Aleman1","Mihnea");
        this.teacher=new Teacher("John","Doe");
        this.teacher1=new Teacher("John1","Doe");
        this.controller.getRegistrationSystem().getStudentRepo().save(student);
        this.controller.getRegistrationSystem().getStudentRepo().save(student1);
        this.controller.getRegistrationSystem().getTeacherRepo().save(teacher);
        UUID course_id=UUID.fromString(this.controller.addCourse(teacher.getId().toString(),"BD","30","6"));
        this.course=this.controller.getRegistrationSystem().getCourseRepo().findOne(course_id);
    }

    /**
     * test for {@link Controller#register(Course, String)}
     */
    @Test
    void register() throws StudentCreditsOverflowException, IOException, MaxEnrollmentCourseException, StudentAlreadyEnrolled {
        assert this.controller.register(this.course,this.student.getStudentId().toString()).equals(this.course.getId().toString());
        
         assertThrows(StudentAlreadyEnrolled.class,()-> this.controller.register(this.course,this.student.getStudentId().toString()));

         this.course.setMaxEnrollment(1);
        assertThrows(MaxEnrollmentCourseException.class,()-> this.controller.register(this.course,this.student1.getStudentId().toString()));

        this.course.setMaxEnrollment(3);
        this.student1.setTotalCredits(29);
        assertThrows(StudentCreditsOverflowException.class,()-> this.controller.register(this.course,this.student1.getStudentId().toString()));

    }

    /**
     * test for {@link Controller#retrieveStudentsEnrolledForACourse(String)}
     */
    @Test
    void retrieveStudentsEnrolledForACourse() {
        List<Student> studentList=new ArrayList<>();
        assertEquals(studentList,this.controller.retrieveStudentsEnrolledForACourse(this.course.getId().toString()));

        studentList.add(student);
        try {
            this.controller.register(this.course,student.getStudentId().toString());
        } catch (StudentCreditsOverflowException | StudentAlreadyEnrolled | IOException | MaxEnrollmentCourseException e) {
            e.printStackTrace();
        }
        assertEquals(studentList,this.controller.retrieveStudentsEnrolledForACourse(this.course.getId().toString()));
    }

    /**
     * test for {@link Controller#addCourse(String, String, String, String)}
     */
    @Test
    void addCourse() throws IOException {
        assertEquals(this.controller.addCourse(this.teacher.getId().toString(),"MAP","30","6"),this.controller.getAllCourses().get(1).getId().toString());
    }

    /**
     * test for {@link Controller#deleteCourse(String, String)}
     */
    @Test
    void deleteCourse() throws UnAuthorizedDeleteCourseException, IOException {
        assertThrows(UnAuthorizedDeleteCourseException.class,()-> this.controller.deleteCourse(this.course.getId().toString(),this.student.getStudentId().toString()));
        this.controller.deleteCourse(this.course.getId().toString(),this.teacher.getId().toString());
        assertNull(this.controller.getRegistrationSystem().getCourseRepo().findOne(this.course.getId()));
    }

    /**
     * test for {@link Controller#addPerson(String, String, String)}
     */
    @Test
    void addPerson() throws IOException {
        UUID person_id=this.controller.addPerson("fname","lname","1");
        assertInstanceOf(Student.class,this.controller.getRegistrationSystem().getStudentRepo().findOne(person_id));

        person_id=this.controller.addPerson("fname","lname","2");
        assertInstanceOf(Teacher.class,this.controller.getRegistrationSystem().getTeacherRepo().findOne(person_id));
    }

    /**
     * test for {@link Controller#changeCredits(String, String, String)}
     */
    @Test
    void changeCredits() throws UnAuthorizedEditCourseException, StudentCreditsOverflowException, IOException {
        assertThrows(UnAuthorizedEditCourseException.class,()-> this.controller.changeCredits(this.teacher1.getId().toString(),
                this.course.getId().toString(),
                "5"));
        try {
            assert this.controller.register(this.course,this.student.getStudentId().toString()).equals(this.course.getId().toString());
        } catch (StudentCreditsOverflowException | MaxEnrollmentCourseException | IOException | StudentAlreadyEnrolled e) {
            e.printStackTrace();
        }
        assertThrows(StudentCreditsOverflowException.class,()-> this.controller.changeCredits(this.teacher.getId().toString(),this.course.getId().toString(),"31"));

        this.controller.changeCredits(this.teacher.getId().toString(),this.course.getId().toString(),"7");
        assertEquals(this.course.getCredits(),7);

    }




}