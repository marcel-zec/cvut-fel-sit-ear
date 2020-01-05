package cz.cvut.fel.ear.hamrazec.dormitory.service.security;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.ManagerDao;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotAllowedException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Block;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Manager;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Role;
import cz.cvut.fel.ear.hamrazec.dormitory.model.User;
import cz.cvut.fel.ear.hamrazec.dormitory.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccessService {
    private ManagerDao managerDao;

    @Autowired
    public AccessService(ManagerDao managerDao) {
        this.managerDao = managerDao;
    }

    public void managerAccess(Block block) throws NotAllowedException, NotFoundException {
        final User currentUser = SecurityUtils.getCurrentUser();
        if (block == null) throw new NotFoundException();
        if (currentUser.getRole().equals(Role.MANAGER)) {
            Manager manager = managerDao.find(currentUser.getId());
            if (!manager.getBlocks().contains(block)) { throw new NotAllowedException("Access denied."); }
        }
    }

    public void studentAccess(Long student_id) throws NotAllowedException {

        final User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser.getRole().equals(Role.STUDENT)) {
            if (!currentUser.getId().equals(student_id)) throw new NotAllowedException("Access denied.");
        }
    }
}
