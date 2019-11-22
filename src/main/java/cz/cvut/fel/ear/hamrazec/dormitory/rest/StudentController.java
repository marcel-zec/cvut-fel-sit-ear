package cz.cvut.fel.ear.hamrazec.dormitory.rest;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.StudentDao;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.AlreadyExistsException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Student;
import cz.cvut.fel.ear.hamrazec.dormitory.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityExistsException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/students")
@Validated
public class StudentController {

    //TODO - pristup len pre managera a superuser

    private static final Logger LOG = LoggerFactory.getLogger(StudentController.class);

    private StudentService studentService;


    @Autowired
    public StudentController(StudentService service) {

        this.studentService = service;
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Student> getStudents() {

        return studentService.findAll();
    }


    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Student getStudent(@PathVariable Long id) throws NotFoundException {

        Student student = studentService.find(id);
        if (student == null) throw new NotFoundException();
        return student;
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createStudent(@RequestBody Student student) {

        try {
            studentService.create(student);
            LOG.info("Student with id {} created.", student.getId());
        } catch (EntityExistsException e) {
            //TODO - exceptions
        }
    }


    @DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeStudent(@PathVariable Long id) {

        try {
            studentService.delete(id);
            LOG.info("Student with id {} removed.", id);
        } catch (NotFoundException e) {
            //TODO - exceptions
        } catch (Exception e) {
            //TODO - exceptions
        }
    }


    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStudent(@PathVariable Long id, @RequestBody Student student) {

        try {
            studentService.update(id, student);
            LOG.info("Student with id {} updated.", id);
        } catch (NotFoundException e) {
            //TODO - exceptions
        }
    }


}
