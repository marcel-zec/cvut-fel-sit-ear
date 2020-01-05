package cz.cvut.fel.ear.hamrazec.dormitory.service.security;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.ManagerDao;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotAllowedException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Manager;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Role;
import cz.cvut.fel.ear.hamrazec.dormitory.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccessService {
    private ManagerDao managerDao;

    @Autowired
    public AccessService(ManagerDao managerDao) {
        this.managerDao = managerDao;
    }

    public void managerAccess(User currentUser, String blockName) throws NotAllowedException {
        if (currentUser.getRole().equals(Role.MANAGER)) {
            Manager manager = managerDao.find(currentUser.getId());
            if (manager.getBlocks() == null || manager.getBlocks().size()==0) { throw new NotAllowedException("Access denied."); }
            if ( manager.getBlocks().stream().anyMatch(block -> !block.getName().equals(blockName))) throw new NotAllowedException("Access denied.");
        }
    }
}
