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
    private final AccommodationService accommodationService;


    @Autowired
    public StudentService(StudentDao studentDao, AccommodationService accommodationService) {

        this.studentDao = studentDao;
        this.accommodationService = accommodationService;
    }


    public List<Student> findAll() {

        return studentDao.findAll();
    }


    public Student find(Long id) {

        return studentDao.find(id);
    }

    @Transactional
    public void create(Student student) throws NotAllowedException {
        if (student.getRole() != Role.STUDENT) throw new NotAllowedException("You are not allowed to create SUPERADMIN.");
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
    public void delete(Long id) throws NotFoundException, NotAllowedException {

        Student student = studentDao.find(id);
        if (student == null) throw new NotFoundException();
        if (student.hasActiveAccommodation()) {
            throw new NotAllowedException("You cannot delete student with active accommodation.");
        } else {
            student.getAccommodations().stream()
                    .filter(accommodation -> accommodation.getStatus().equals(Status.PENDING) || accommodation.getStatus().equals(Status.APPROVED))
                    .findFirst().ifPresent(accommodationService::cancelAccommodation);

//            for (Accommodation accommodation: student.getAccommodations()) {
//                Status status = accommodation.getStatus();
//                if (status.equals(Status.APPROVED) || status.equals(Status.PENDING)) accommodationService.cancelReservation(accommodation);
//            }
            studentDao.remove(student);
        }
    }


}
