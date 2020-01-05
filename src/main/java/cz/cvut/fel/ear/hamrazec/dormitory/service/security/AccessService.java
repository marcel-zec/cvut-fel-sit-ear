package cz.cvut.fel.ear.hamrazec.dormitory.service.security;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.ManagerDao;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.BadPassword;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotAllowedException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Block;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Manager;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Role;
import cz.cvut.fel.ear.hamrazec.dormitory.model.User;
import cz.cvut.fel.ear.hamrazec.dormitory.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccessService {
    private final ManagerDao managerDao;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;

    @Autowired
    public AccessService(ManagerDao managerDao,PasswordEncoder passwordEncoder,  JavaMailSender javaMailSender) {
        this.managerDao = managerDao;
        this.passwordEncoder = passwordEncoder;
        this.javaMailSender = javaMailSender;
    }

    @Transactional
    public void managerAccess(Block block) throws NotAllowedException, NotFoundException {
        final User currentUser = SecurityUtils.getCurrentUser();
        if (block == null) throw new NotFoundException();
        if (currentUser.getRole().equals(Role.MANAGER)) {
            Manager manager = managerDao.find(currentUser.getId());
            if (!manager.getBlocks().contains(block)) { throw new NotAllowedException("Access denied."); }
        }
    }

    @Transactional
    public void studentAccess(Long student_id) throws NotAllowedException {

        final User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser.getRole().equals(Role.STUDENT)) {
            if (!currentUser.getId().equals(student_id)) throw new NotAllowedException("Access denied.");
        }
    }

    @Transactional
    public void changePassword(String oldPassword, String newPassword, String newPasswordAgain) throws BadPassword {

        final User currentUser = SecurityUtils.getCurrentUser();
        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            throw new BadPassword();
        }else {
            if (newPassword.equals(newPasswordAgain)) {
                currentUser.setPassword(new BCryptPasswordEncoder().encode(newPassword));
                SimpleMailMessage msg = new SimpleMailMessage();
                msg.setTo(currentUser.getEmail());
                msg.setSubject("Password change");
                msg.setText("Hello your password change.\n" + "If you're not change it, contact us as soon as possible." +
                        "\n \n With love IT team.");
                javaMailSender.send(msg);
            }
        }
    }
}
