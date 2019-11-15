package cz.cvut.fel.ear.hamrazec.dormitory.rest;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.StudentDao;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Student;
import cz.cvut.fel.ear.hamrazec.dormitory.model.UserRole;
import cz.cvut.fel.ear.hamrazec.dormitory.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/students")
@Validated
public class StudentController {

    private static final Logger LOG = LoggerFactory.getLogger(StudentController.class);

    private StudentDao studentDao;

    @Autowired
    public StudentController(StudentDao studentDao) {
        this.studentDao = studentDao;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    //TODO - len pre managera
    public List<Student> getStudents(){
        return studentDao.findAll();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Student getStudent(@PathVariable Long id) {
        Student student = studentDao.find(id);
        //TODO - exception ak nenajde
        return student;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createStudent(@RequestBody Student student) {
        studentDao.persist(student);
        LOG.info("Student with id {} created.", student.getId());
    }

    @DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeStudent(@PathVariable Long id) {
        Student student = studentDao.find(id);
        //TODO - servisa na kontrolu ci moze byt student vymazany (ci nema aktivne ubytovanie atd.)
        //TODO - exception ak nenajde
        studentDao.remove(student);
        LOG.info("Student with id {} removed.", id);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStudent(@PathVariable Long id, @RequestBody Map<String,String> request) {
        //TODO - exception ak nenajde
        //TODO - servisa na update studenta
        LOG.info("Student with id {} updated.", id);
    }




}
