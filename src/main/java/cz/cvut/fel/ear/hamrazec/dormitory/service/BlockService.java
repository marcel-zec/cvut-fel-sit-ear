package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.BlockDao;
import cz.cvut.fel.ear.hamrazec.dormitory.dao.ManagerDao;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.AlreadyExistsException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.BadFloorException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotAllowedException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlockService {

    private final BlockDao blockDao;
    private final ManagerDao managerDao;
    private final RoomService roomService;


    @Autowired
    public BlockService(BlockDao blockDao, ManagerDao managerDao, RoomService roomService) {

        this.blockDao = blockDao;
        this.managerDao = managerDao;
        this.roomService = roomService;
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
    public List<Room> getRoomsAtFloor(String blockName, Integer floorNumber) throws NotFoundException {
        Block block = blockDao.find(blockName);
        if (block == null) throw new NotFoundException();
        return block.getRooms().stream().filter(room -> room.getFloor().equals(floorNumber)).collect(Collectors.toList());
    }


    @Transactional
    public void update(String blockName, String name, String address) throws NotFoundException {
        Block block = blockDao.find(blockName);
        if (block == null) throw new NotFoundException();

        if (name != null) block.setName(name);
        if (address != null) block.setAddress(address);
    }


    @Transactional
    public void changeAmountOfFloors(String blockName, Integer amount) throws NotFoundException, BadFloorException, NotAllowedException {
        Block block = blockDao.find(blockName);
        if (block == null) throw new NotFoundException();
        if (amount > 20 || amount < 0) throw new BadFloorException("Amount of floors should be number between 0-20.");
        if (block.getFloors() > amount) {
            List<Room> roomsForDelete = new ArrayList<>(block.getRooms().stream().filter(room -> room.getFloor() > amount).collect(Collectors.toList()));
            for (Room toDelete: roomsForDelete) {
                roomService.deleteRoom(toDelete);
            }
        }
        block.setFloors(amount);
        blockDao.update(block);
    }

    @Transactional
    public void delete(String blockName) throws NotFoundException, NotAllowedException {

        Block block = blockDao.find(blockName);
        if (block == null) throw new NotFoundException();
        List<Room> toDeleteList;
        if (block.getRooms()!= null)  {
            toDeleteList = new ArrayList<>(block.getRooms());
        for (Room toDelete: toDeleteList) {
            roomService.deleteRoom(toDelete);
            }
        }
        block.softDelete();
        blockDao.update(block);
    }
}
