package com.company.Repository;


import com.company.Model.Student;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * this class implements the logic for the Repository for model Student
 */
public class StudentRepo extends FileRepo<Student> {

    public StudentRepo() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Student findOne(@NotNull UUID id) {
        for (Student student:this.repoList //loop through the repo to find the matching id
             ) {
            if(student.getStudentId().equals(id)){
                return student;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Student delete(@NotNull UUID id) {
        for (Student student:this.repoList //loop through the repo to find the matching id
             ) {
            if(student.getStudentId().equals(id)){
                this.repoList.remove(student);
                return student;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Student update(@NotNull Student entity){
        for (Student student:this.repoList
             ) {
            if(student.getStudentId().equals(entity.getStudentId())) { //loop through the repo to find the matching entity
                student.setEnrolledCourses(entity.getEnrolledCourses());
                student.setTotalCredits(entity.getTotalCredits());
                student.setFirstName(entity.getFirstName());
                student.setLastName(entity.getLastName());
                return null;
            }
        }
        return entity;
    }

    /**
     * this method is for reading Repository Data from a file
     * @throws IOException Input or Output error when using a file
     */
    @Override
    public void readFromFile() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        this.repoList= new ArrayList<>(Arrays.asList(objectMapper.readValue(new File("src\\main\\resources\\"+Student.class.getName()+".json"),Student[].class)));
    }

}
