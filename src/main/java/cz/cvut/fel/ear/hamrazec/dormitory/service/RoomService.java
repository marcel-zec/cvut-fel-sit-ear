package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.BlockDao;
import cz.cvut.fel.ear.hamrazec.dormitory.dao.ManagerDao;
import cz.cvut.fel.ear.hamrazec.dormitory.dao.RoomDao;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Block;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Manager;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Room;
import javassist.bytecode.analysis.ControlFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class RoomService {

    private final BlockDao blockDao;
    private final RoomDao roomDao;


    @Autowired
    public RoomService(BlockDao blockDao, RoomDao roomDao) {

        this.blockDao = blockDao;
        this.roomDao = roomDao;
    }


    public List<Room> findAll(String blockName) throws NotFoundException {

        Block block = blockDao.find(blockName);
        if (block == null) throw new NotFoundException();
        return block.getRooms();
    }


    public Block find(String blockName, Integer roomNumber) {
        //TODO - query
        return null;
    }

    @Transactional
    public void addRoom(String blockName, Room room) throws NotFoundException {
        Block block = blockDao.find(blockName);
        if (block == null) throw new NotFoundException();
        block.addRoom(room);
        blockDao.update(block);
    }

}
