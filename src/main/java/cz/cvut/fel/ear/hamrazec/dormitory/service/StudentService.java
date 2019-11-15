package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.StudentDao;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Gender;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class StudentService {

    private final StudentDao studentDao;


    @Autowired
    public StudentService(StudentDao studentDao) {

        this.studentDao = studentDao;
    }


    public List<Student> findAll() {

        return studentDao.findAll();
    }


    public Student find(Long id) {

        return studentDao.find(id);
    }


    public void create(Student student) {

        studentDao.persist(student);
    }


    public void update(Long id, Map<String, String> request) throws NotFoundException {

        Student student = studentDao.find(id);
        if (student == null) throw new NotFoundException();
        if (request.containsKey("firstName")) student.setFirstName(request.get("firstName"));
        if (request.containsKey("lastName")) student.setLastName(request.get("lastName"));
        if (request.containsKey("username")) student.setUsername(request.get("username"));
        if (request.containsKey("email")) student.setEmail(request.get("email"));
        if (request.containsKey("university")) student.setUniversity(request.get("university"));
        //TODO - konverzia zo String na LocalDate
//        if (request.containsKey("birth")) student.setBirth(request.get("birth"));
        if (request.containsKey("bankAccountNumber")) student.setBankAccountNumber(request.get("bankAccountNumber"));
//        if (request.containsKey("endOfStudy")) student.setEndOfStudy(request.get("endOfStudy"));
        if (request.containsKey("gender")) student.setGender(Gender.valueOf(request.get("gender")));
    }


    public void delete(Long id) throws NotFoundException, Exception {

        Student student = studentDao.find(id);
        if (student == null) throw new NotFoundException();
        if (false) {
            //TODO - ak ma aktivne ubytovanie tak nemoze vymazat
            throw new Exception();
        } else {
            studentDao.remove(student);
        }
    }


}
