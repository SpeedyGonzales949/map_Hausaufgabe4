package com.company.Controller;

import com.company.Exceptions.MaxEnrollmentCourseException;
import com.company.Exceptions.StudentCreditsOverflowException;
import com.company.Model.Course;
import com.company.Model.Person;
import com.company.Model.Student;
import com.company.Model.Teacher;
import com.company.Repository.CourseRepo;
import com.company.Repository.StudentRepo;
import com.company.Repository.TeacherRepo;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * this class handles the data flow
 */
public class RegistrationSystem {
    protected StudentRepo studentRepo;
    protected TeacherRepo teacherRepo;
    protected CourseRepo courseRepo;

    public RegistrationSystem(){
        this.studentRepo=new StudentRepo();
        this.teacherRepo=new TeacherRepo();
        this.courseRepo=new CourseRepo();
    }
    public StudentRepo getStudentRepo() {
        return studentRepo;
    }

    public TeacherRepo getTeacherRepo() {
        return teacherRepo;
    }

    public CourseRepo getCourseRepo() {
        return courseRepo;
    }

    /**
     * register a student to a course
     * @param course , must not be null
     * @param student , must not be null
     * @return true if student was registered to specified course, false if course has no more free places
     */
    public boolean register(@NotNull Course course,@NotNull Student student) throws StudentCreditsOverflowException, MaxEnrollmentCourseException, IOException {
        if(course.getStudentsEnrolled().size()< course.getMaxEnrollment()){
            if(student.getTotalCredits()+course.getCredits()<=30){

                if(!student.getEnrolledCourses().contains(course.getId())){
                    course.getStudentsEnrolled().add(student.getStudentId());
                    student.getEnrolledCourses().add(course.getId());
                    student.setTotalCredits(student.getTotalCredits()+course.getCredits());
                    this.writeData();
                    return true;
                }else{
                    return false;
                }
            }else{
                throw new StudentCreditsOverflowException("Courses credits exceeded");
            }

        }else{
            throw new MaxEnrollmentCourseException("Max Enrollment in this Course exceeded");
        }
    }

    /**
     * this method retrieves all Courses with free Place from {@link RegistrationSystem#courseRepo}
     * @return {@code List<Course>}
     */
    public List<Course> retrieveCoursesWithFreePlaces(){
        //(Filter+Sort)-Function
        List<Course> courses=this.getAllCourses().stream().filter(course -> course.getMaxEnrollment()-course.getStudentsEnrolled().size()>0).collect(Collectors.toList());
        Comparator<Course> comparatorbyPopularity= Comparator.comparing((Course course) -> String.valueOf(course.getStudentsEnrolled().size()));
        courses.sort(comparatorbyPopularity.reversed());
        return courses;
    }

    /**
     * this method retrieves all the students enrolled for a course
     * @param course Course, for whom students are enrolled
     * @return {@code List<Student>}
     */
    public List<Student> retrieveStudentsEnrolledForACourse(@NotNull Course course){
        //(Filter+Sort)-Function
        List<Student> students = ((List<Student>) this.studentRepo.findAll()).stream().filter(student -> student.getEnrolledCourses().contains(course.getId())).collect(Collectors.toList());
        Comparator<Student> comparatorbyName= Comparator.comparing((Student student) -> (student.getLastName() + student.getFirstName()));
        students.sort(comparatorbyName.reversed());
        return students;
    }

    public List<Course> getAllCourses(){
        return (List<Course>) courseRepo.findAll();
    }

    /**
     * this method adds a new course to {@link RegistrationSystem#courseRepo}
     * @param teacher Teacher of the Course
     * @param course the new created Course
     * @return true, if operation was successful
     * @throws IOException Input or Output error when using a file
     */
    public boolean addCourse(@NotNull Teacher teacher,@NotNull Course course) throws IOException {
        teacherRepo.save(teacher);
        courseRepo.save(course);
        teacher.addCourse(course);
        this.writeData();
        return true;
    }

    /**
     * this method adds a new Student to {@link RegistrationSystem#studentRepo}
     * @param student new Student
     * @return unique Identifier of the student
     * @throws IOException Input or Output error when using a file
     */
    public UUID addStudent(@NotNull Student student) throws IOException {
        this.studentRepo.save(student);
        this.writeData();
        return student.getStudentId();
    }

    /**
     * this method adds a new Teacher to {@link RegistrationSystem#teacherRepo}
     * @param teacher new Teacher
     * @return unique Identifier of the teacher
     * @throws IOException Input or Output error when using a file
     */
    public UUID addTeacher(@NotNull Teacher teacher) throws IOException {
        this.teacherRepo.save(teacher);
        this.writeData();
        return teacher.getId();
    }

    /**
     * this method searches for a user in {@link RegistrationSystem#studentRepo} or {@link RegistrationSystem#teacherRepo}
     * @param id unique identifier of the person
     * @return the person, that was found
     */
    public Person findPerson(UUID id){
        Person person = studentRepo.findOne(id);
        if(person!=null){
            return person;
        }
        person=teacherRepo.findOne(id);
        return person;
    }

    /**
     * this method searches for a course in {@link RegistrationSystem#courseRepo}
     * @param id unique identifier of the course
     * @return the course,that was found
     */
    public Course findCourse(UUID id){
        return this.courseRepo.findOne(id);
    }

    /**
     * this method changes the number of credits of a course
     * @param course the course, which details will be changed
     * @param credits the new number of credits
     * @throws StudentCreditsOverflowException Can't change, because students enrolled to this course would exceed their limit
     * @throws IOException Input or Output error when using a file
     */
    public void changeCredits(Course course,int credits) throws StudentCreditsOverflowException, IOException {
        int difference=course.getCredits()-credits;
        if(difference<0){
            for (UUID id:course.getStudentsEnrolled()){
                Student student=this.studentRepo.findOne(id);
                if(student.getTotalCredits()-difference>30)
                    throw new StudentCreditsOverflowException("Student maximal credits limit exceeded");
            }
        }
        course.setCredits(credits);
        for (UUID id:course.getStudentsEnrolled()){
            Student student=this.studentRepo.findOne(id);
            student.setTotalCredits(student.getTotalCredits()-difference);
        }
        this.writeData();

    }

    /**
     * this method deletes a course from
     * @param course the course, that will be deleted
     * @throws IOException Input or Output error when using a file
     */
    public void deleteCourse(Course course) throws IOException {
        this.courseRepo.delete(course.getId());
        for (Student student :this.studentRepo.findAll()
        ) {
            student.getEnrolledCourses().remove(course.getId());
            student.setTotalCredits(student.getTotalCredits()-course.getCredits());
        }
        for (Teacher teacher:this.teacherRepo.findAll()
             ) {
            teacher.getCourses().remove(course.getId());
        }
        this.writeData();
    }

    /**
     * this method reads data from file and saves into the repo {@link RegistrationSystem#courseRepo,RegistrationSystem#teacherRepo,RegistrationSystem#studentRepo}
     * @throws IOException Input or Output error when using a file
     */
    public void readData() throws IOException {
        this.studentRepo.readFromFile();
        this.teacherRepo.readFromFile();
        this.courseRepo.readFromFile();
    }

    /**
     * this method saves the data to a file
     * @throws IOException Input or Output error when using a file
     */
    public void writeData() throws IOException {
        this.studentRepo.saveToFile();
        this.teacherRepo.saveToFile();
        this.courseRepo.saveToFile();

    }

}
