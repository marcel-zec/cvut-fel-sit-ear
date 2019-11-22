package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.StudentDao;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotAllowedException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Gender;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Role;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

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

    @Transactional
    public void create(Student student) throws NotAllowedException {
        if (student.getRole() != Role.STUDENT) throw new NotAllowedException();
        studentDao.persist(student);
    }

    @Transactional
    public void update(Long id, Student student) throws NotFoundException {
        Student studentToUpdate = studentDao.find(id);
        if (studentToUpdate == null) throw new NotFoundException();

        studentToUpdate.setFirstName(student.getFirstName());
        studentToUpdate.setLastName(student.getLastName());
        studentToUpdate.setUsername(student.getUsername());
        studentToUpdate.setEmail(student.getEmail());
        studentToUpdate.setGender(student.getGender());
        studentToUpdate.setEndOfStudy(student.getEndOfStudy());
        studentToUpdate.setBankAccountNumber(student.getBankAccountNumber());
        studentToUpdate.setBirth(student.getBirth());
        studentToUpdate.setUniversity(student.getUniversity());
        studentDao.update(studentToUpdate);
    }

    @Transactional
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
