package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.StudentDao;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotAllowedException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.*;
import cz.cvut.fel.ear.hamrazec.dormitory.security.SecurityUtils;
import cz.cvut.fel.ear.hamrazec.dormitory.service.security.AccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentService {

    private final StudentDao studentDao;
    private final PasswordService passwordService;
    private final AccessService accessService;
    private final JavaMailSender javaMailSender;


    @Autowired
    public StudentService(StudentDao studentDao, PasswordService passwordService, AccessService accessService, JavaMailSender javaMailSender) {
        this.studentDao = studentDao;
        this.passwordService = passwordService;
        this.accessService = accessService;
        this.javaMailSender = javaMailSender;
    }


    public List<Student> findAll() {
        return studentDao.findAll();
    }


    public Student find(Long id) {
        return studentDao.find(id);
    }

    public Student findMe() {
        final User currentUser = SecurityUtils.getCurrentUser();
        return studentDao.find(currentUser.getId());
    }

    @Transactional
    public void create(Student student) {
        String password = passwordService.generatePassword();
        student.setPassword(new BCryptPasswordEncoder().encode(password));
        studentDao.persist(student);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(student.getEmail());
        msg.setSubject("Dormitory system");
        msg.setText("Hello at dormitory system \n Your password is: " + password + "\n Please change it as soon as possible." +
                "\n \n With love IT team.");
        javaMailSender.send(msg);
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
            student.softDelete();
            studentDao.update(student);
        }
    }


}
