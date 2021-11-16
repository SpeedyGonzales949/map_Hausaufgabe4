package com.company.Repository;

import com.company.Model.Course;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * this class implements the logic for the Repository for model Course
 */
public class CourseRepo extends FileRepo<Course> {
    public CourseRepo() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Course findOne(@NotNull UUID id) {//loop through the repo to find the matching id
        for(Course course:this.repoList){
            if(course.getId().equals(id))
                return course;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Course delete(@NotNull UUID id) {//loop through the repo to find the matching id
        for (Course course:this.repoList
             ) {
            if(course.getId()==id){
                this.repoList.remove(course);
                return course;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Course update(@NotNull Course entity) { //loop through the repo to find the matching entity
        for (Course course:this.repoList
             ) {
            if (course.getId()==entity.getId()){
                course.setCredits(entity.getCredits());
                course.setMaxEnrollment(entity.getMaxEnrollment());
                course.setName(entity.getName());
                course.setTeacher(entity.getTeacher());
                course.setStudentsEnrolled(entity.getStudentsEnrolled());
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
        this.repoList= new ArrayList<>(Arrays.asList(objectMapper.readValue(new File("src\\main\\resources\\"+Course.class.getName()+".json"),Course[].class )));
    }

}
