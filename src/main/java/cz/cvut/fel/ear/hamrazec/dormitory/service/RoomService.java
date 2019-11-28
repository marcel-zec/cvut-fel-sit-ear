package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.BlockDao;
import cz.cvut.fel.ear.hamrazec.dormitory.dao.ManagerDao;
import cz.cvut.fel.ear.hamrazec.dormitory.dao.RoomDao;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Accommodation;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Block;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Manager;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Room;
import javassist.bytecode.analysis.ControlFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RoomService {

    private final BlockDao blockDao;
    private final RoomDao roomDao;
    private List<Room> roomList = new ArrayList<Room>();
    private AccommodationService accommodationService;

    @Autowired
    public RoomService(BlockDao blockDao, RoomDao roomDao, AccommodationService accommodationService) {

        this.blockDao = blockDao;
        this.roomDao = roomDao;
        this.accommodationService = accommodationService;
    }


    @Transactional
    public List<Room> findFreeRooms(String blockName, LocalDate dateStart, LocalDate dateEnd) throws NotFoundException {

        Block block = blockDao.find(blockName);
        if (block == null) throw new NotFoundException();
        for (Room room: block.getRooms()) {

            accommodationService.updateExpired(room.getActualAccommodations());
            //todo urobit update expirovanych ubytovani
            //todo zistit ci je v dany datum volna izba

            if (room.getActualAccommodations().size() < room.getMaxCapacity()) {
                roomList.add(room);
            }
        }
        return roomList;
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

    @Transactional
    public void cancelActualAccomodation(){

    }


}
