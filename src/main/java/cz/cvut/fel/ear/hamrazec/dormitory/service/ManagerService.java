package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.ManagerDao;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Manager;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ManagerService {

    private ManagerDao managerDao;


    @Autowired
    public ManagerService(ManagerDao managerDao) {

        this.managerDao = managerDao;
    }

    @Transactional
    public void create(Manager manager) {

        managerDao.persist(manager);
    }

    @Transactional
    public void update(Long id, Map<String, String> map) throws NotFoundException {
       Manager manager = managerDao.find(id);
       if (manager == null) throw new NotFoundException("");

       if (map.containsKey("firstName")) manager.setFirstName(map.get("firstName"));
       if (map.containsKey("lastName")) manager.setLastName(map.get("lastName"));
       if (map.containsKey("username")) manager.setUsername(map.get("username"));
       if (map.containsKey("email")) manager.setEmail(map.get("email"));

       managerDao.update(manager);
    }

    @Transactional(readOnly = true)
    public Manager find(Long id) {

        return managerDao.find(id);
    }

    @Transactional(readOnly = true)
    public List<Manager> findAll() {

        return managerDao.findAll();
    }

    @Transactional(readOnly = true)
    public List<Manager> findAllByBlock(String blockName) {
        //TODO - findManagerByBlockName
        return managerDao.findAll();
    }

    @Transactional
    public void delete(Long id) {

        Manager manager = managerDao.find(id);
        managerDao.remove(manager);
    }
}
