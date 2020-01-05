package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.ManagerDao;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.BadPassword;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotAllowedException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Block;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Manager;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Role;
import cz.cvut.fel.ear.hamrazec.dormitory.model.User;
import cz.cvut.fel.ear.hamrazec.dormitory.security.SecurityUtils;
import cz.cvut.fel.ear.hamrazec.dormitory.service.security.AccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OrderBy;
import java.util.HashMap;
import java.util.List;

@Service
public class ManagerService {

    private final ManagerDao managerDao;
    private final BlockService blockService;
    private final PasswordService passwordService;
    private final JavaMailSender javaMailSender;


    @Autowired
    public ManagerService(ManagerDao managerDao, BlockService blockService, PasswordService passwordService,
                          JavaMailSender javaMailSender, AccessService accessService) {

        this.managerDao = managerDao;
        this.blockService = blockService;
        this.passwordService = passwordService;
        this.javaMailSender = javaMailSender;
    }


    @Transactional
    public void create(Manager manager) {
        String password = passwordService.generatePassword();
        manager.setPassword(new BCryptPasswordEncoder().encode(password));
        manager.setWorkerNumber(getNextWorkerNumber());
        managerDao.persist(manager);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(manager.getEmail());
        msg.setSubject("Dormitory system");
        msg.setText("Hello at dormitory system as new Manager\n Your password is: " + password + "\n Please change it as soon as possible." +
                "\n \n With love IT team.");
        javaMailSender.send(msg);
    }


    @Transactional
    public void update(Integer workerNumber, Manager manager) throws NotFoundException, NotAllowedException {

        Manager toUpdate = managerDao.findByWorkerNumber(workerNumber);
        if (manager == null) throw new NotFoundException();
        if (manager.getRole() != null && manager.getRole() != Role.MANAGER) throw new NotAllowedException("You are not allowed to change role.");

        toUpdate.setFirstName(manager.getFirstName());
        toUpdate.setLastName(manager.getLastName());
        toUpdate.setUsername(manager.getUsername());
        toUpdate.setEmail(manager.getEmail());

        managerDao.update(toUpdate);
    }

    @Transactional(readOnly = true)
    public Manager find(Integer workerNumber) {

        return managerDao.findByWorkerNumber(workerNumber);
    }

    @Transactional(readOnly = true)
    public Manager findMe() {
        final User currentUser = SecurityUtils.getCurrentUser();
        return managerDao.find(currentUser.getId());
    }


    @Transactional(readOnly = true)
    public List<Manager> findAll() {
        return managerDao.findAll();
    }


    @Transactional(readOnly = true)
    public List<Manager> findAllByBlock(String blockName) throws NotFoundException {

        Block block = blockService.find(blockName);
        if (block == null) throw new NotFoundException();
        return block.getManagers();
    }


    @Transactional
    public void delete(Integer workerNumber) throws NotFoundException {

        Manager manager = managerDao.findByWorkerNumber(workerNumber);
        if (manager == null) throw new NotFoundException();
        for (Block block : manager.getBlocks()) {
            block.removeManager(manager);
        }
        manager.softDelete();
        managerDao.update(manager);
    }

    @Transactional
    public int getNextWorkerNumber(){
        List<Manager> managers = managerDao.findAll(true);
        if (managers == null || managers.isEmpty()) return 1;
        else {
           return managers.stream().reduce((manager, manager2) -> manager.getWorkerNumber() > manager2.getWorkerNumber() ? manager : manager2).get().getWorkerNumber() + 1;
        }
    }
}
