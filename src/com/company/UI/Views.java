package com.company.UI;

import com.company.Controller.Controller;
import com.company.Model.Course;
import com.company.Model.Student;
import org.jetbrains.annotations.NotNull;


import java.util.List;

/**
 * this class implements the view of the output data in the console
 */
public class Views {
    /**
     * thsi method implements the special View for {@link Controller#retrieveStudentsEnrolledForACourse(String)}
     * @param students Students enrolled for the course
     */
    public static void retrieveStudentsEnrolledForACourseView(@NotNull List<Student> students){
        System.out.println("Students:");
        for (Student student:students){
            System.out.println(student.getFirstName()+" "+student.getLastName());
        }
    }

    /**
     * this method implements the special View for {@link Controller#retrieveCoursesWithFreePlaces()}
     * @param courses Courses with free places
     */
    public static void retrieveCoursesWithFreePlacesView(@NotNull List<Course>courses){
        for (Course course:courses
        ) {
            System.out.println("Name:"+course.getName()+" Free Places:"+ (course.getMaxEnrollment() - course.getStudentsEnrolled().size()) +"/"+course.getMaxEnrollment());
        }
    }

    /**
     * this method implements the special View for {@link Controller#register(Course, String)}
     * @param courses Courses for regisyering
     */
    public static void registerForCourseView(@NotNull List<Course>courses){
        Integer number= 1;
        for (Course course:courses){
            System.out.println(number+"."+course.getName()+" Free Places:"+ (course.getMaxEnrollment() - course.getStudentsEnrolled().size()));
            number++;
        }
    }
}
