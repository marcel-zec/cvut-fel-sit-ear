package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.BlockDao;
import cz.cvut.fel.ear.hamrazec.dormitory.dao.ManagerDao;
import cz.cvut.fel.ear.hamrazec.dormitory.dao.StudentDao;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.AlreadyExistsException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Block;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Gender;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Manager;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class BlockService {

    private final BlockDao blockDao;
    private final ManagerDao managerDao;


    @Autowired
    public BlockService(BlockDao blockDao, ManagerDao managerDao) {

        this.blockDao = blockDao;
        this.managerDao = managerDao;
    }


    public List<Block> findAll() {

        return blockDao.findAll();
    }


    public Block find(Long id) {

        return blockDao.find(id);
    }


    public Block find(String name) {

        return blockDao.find(name);
    }


    @Transactional
    public void create(Block block) {

        blockDao.persist(block);
    }


    @Transactional
    public void addManager(String blockName, Integer workerNumber) throws NotFoundException, AlreadyExistsException {

        Block block = blockDao.find(blockName);
        Manager manager = managerDao.findByWorkerNumber(workerNumber);
        if (block == null || manager == null) throw new NotFoundException();

        block.addManager(manager);
        manager.addBlock(block);

        blockDao.update(block);
        managerDao.update(manager);

    }

    @Transactional
    public void removeManager(String blockName, Integer workerNumber) throws NotFoundException {

        Block block = blockDao.find(blockName);
        Manager manager = managerDao.findByWorkerNumber(workerNumber);
        if (block == null || manager == null) throw new NotFoundException();

        block.removeManager(manager);
        manager.removeBlock(block);

        blockDao.update(block);
        managerDao.update(manager);

    }


    @Transactional
    public void update(String blockName, String name, String address) throws NotFoundException {
        Block block = blockDao.find(blockName);
        if (block == null) throw new NotFoundException();

        if (name != null) block.setName(name);
        if (address != null) block.setAddress(address);
    }


    @Transactional
    public void delete(String blockName) throws NotFoundException, Exception {

        Block block = blockDao.find(blockName);
        if (block == null) throw new NotFoundException();
        if (false) {
            //TODO - nemezat ak ma izby
            throw new Exception();
        } else {
            blockDao.remove(block);
        }
    }


}
