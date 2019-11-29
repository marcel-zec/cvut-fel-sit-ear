package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.ManagerDao;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Block;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Manager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ManagerService {

    private final ManagerDao managerDao;
    private final BlockService blockService;


    @Autowired
    public ManagerService(ManagerDao managerDao, BlockService blockService) {

        this.managerDao = managerDao;
        this.blockService = blockService;
    }


    @Transactional
    public void create(Manager manager) {

        managerDao.persist(manager);
    }


    @Transactional
    public void update(Integer workerNumber, Manager manager) throws NotFoundException {

        Manager toUpdate = managerDao.findByWorkerNumber(workerNumber);
        if (manager == null) throw new NotFoundException();

        toUpdate.setFirstName(manager.getFirstName());
        toUpdate.setLastName(manager.getLastName());
        toUpdate.setUsername(manager.getUsername());
        toUpdate.setEmail(manager.getEmail());
        toUpdate.setWorkerNumber(manager.getWorkerNumber());

        managerDao.update(toUpdate);
    }


    @Transactional(readOnly = true)
    public Manager find(Integer workerNumber) {

        return managerDao.findByWorkerNumber(workerNumber);
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
        managerDao.remove(manager);
    }
}
