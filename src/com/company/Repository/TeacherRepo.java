package com.company.Repository;

import com.company.Model.Teacher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * this class implements the logic for the Repository for model Teacher
 */
public class TeacherRepo extends FileRepo<Teacher>{
    public TeacherRepo() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Teacher findOne(@NotNull UUID id) {//loop through the repo to find the matching id
        for (Teacher teacher:this.repoList
             ) {
            if (teacher.getId().equals(id))
                return teacher;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Teacher delete(@NotNull UUID id) {//loop through the repo to find the matching id
        for (Teacher teacher:this.repoList
             ) {
            if(teacher.getId().equals(id)){
                this.repoList.remove(teacher);
                return teacher;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Teacher update(@NotNull Teacher entity) { //loop through the repo to find the matching entity
        for (Teacher teacher:this.repoList
             ) {
            if (teacher.getId().equals(entity.getId())){
                teacher.setLastName(entity.getLastName());
                teacher.setFirstName(entity.getFirstName());
                teacher.setCourses(entity.getCourses());
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
        this.repoList= new ArrayList<>(Arrays.asList(objectMapper.readValue(new File("src\\main\\resources\\"+Teacher.class.getName()+".json"),Teacher[].class )));
    }
}
