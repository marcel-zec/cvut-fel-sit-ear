package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.StudentDao;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotAllowedException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentService {

    private final StudentDao studentDao;
    private final ReservationService reservationService;


    @Autowired
    public StudentService(StudentDao studentDao, ReservationService reservationService) {
        this.studentDao = studentDao;
        this.reservationService = reservationService;

    }


    public List<Student> findAll() {
        return studentDao.findAll();
    }


    public Student find(Long id) {
        return studentDao.find(id);
    }


    @Transactional
    public void create(Student student) throws NotAllowedException {
        if (student.getRole() == null) student.setRole(Role.STUDENT);
        if (student.getRole() != Role.STUDENT) throw new NotAllowedException("You are not allowed to change role.");
        student.setPassword(student.getBirth().toString());
        studentDao.persist(student);
    }


    @Transactional
    public void update(Long id, Student student) throws NotFoundException, NotAllowedException {
        Student studentToUpdate = studentDao.find(id);
        if (studentToUpdate == null) throw new NotFoundException();
        if (student.getRole() != Role.STUDENT) throw new NotAllowedException("You are not allowed to change role.");

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
    public void delete(Long id) throws NotFoundException, NotAllowedException {

        Student student = studentDao.find(id);
        if (student == null) throw new NotFoundException();
        if (student.hasActiveAccommodation()) {
            throw new NotAllowedException("You cannot delete student with active accommodation.");
        } else {
            if (student.getReservation() != null){
                student.getReservation().setStatus(Status.RES_CANCELED);
            }
        }
    }


}
