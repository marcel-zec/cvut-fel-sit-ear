package cz.cvut.fel.ear.hamrazec.dormitory.rest;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.StudentDao;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Student;
import cz.cvut.fel.ear.hamrazec.dormitory.service.StudentService;
import org.graalvm.compiler.core.common.type.ArithmeticOpTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/students")
@Validated
public class StudentController {

    //TODO - pristup len pre managera a superuser

    private static final Logger LOG = LoggerFactory.getLogger(StudentController.class);

    private StudentService service;


    @Autowired
    public StudentController(StudentService service) {

        this.service = service;
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Student> getStudents() {

        return service.findAll();
    }


    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Student getStudent(@PathVariable Long id) throws NotFoundException {

        Student student = service.find(id);
        if (student == null) throw new NotFoundException();
        return student;
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createStudent(@RequestBody Student student) {

        service.create(student);
        LOG.info("Student with id {} created.", student.getId());
    }


    @DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeStudent(@PathVariable Long id) {

        try {
            service.delete(id);
            LOG.info("Student with id {} removed.", id);
        } catch (NotFoundException e) {

        } catch (Exception e) {
            //TODO - exceptions
        }
    }


    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStudent(@PathVariable Long id, @RequestBody Map<String, String> request) {

        try {
            service.update(id, request);
            LOG.info("Student with id {} updated.", id);
        } catch (NotFoundException e) {
            //TODO - exceptions
        }
    }


}
