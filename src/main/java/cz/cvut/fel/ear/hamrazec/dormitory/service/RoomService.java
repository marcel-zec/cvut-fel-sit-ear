package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.BlockDao;
import cz.cvut.fel.ear.hamrazec.dormitory.dao.ManagerDao;
import cz.cvut.fel.ear.hamrazec.dormitory.dao.RoomDao;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.*;
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

    public List<Room> findAll(String blockName) throws NotFoundException {
        Block block = blockDao.find(blockName);
        if (block == null) throw new NotFoundException();
        return block.getRooms();
    }


    @Transactional
    public List<Room> findFreeRooms(String blockName, LocalDate dateStart, LocalDate dateEnd) throws NotFoundException {

        Block block = blockDao.find(blockName);
        if (block == null) throw new NotFoundException();
        for (Room room: block.getRooms()) {
            removeEndedActualAccommodation(room);

            //todo zistit ci je v dany datum volna izba


            if (room.getActualAccommodations().size() < room.getMaxCapacity()) {
                roomList.add(room);
            }
            else if (isFreeAtDate(room, dateStart)) roomList.add(room);
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
    public void removeEndedActualAccommodation(Room room){

        for (Accommodation accommodation: room.getActualAccommodations()) {
            if (accommodation.getStatus() == Status.ENDED) {
                room.addPastAccomodation(accommodation);
                room.cancelActualAccomodation(accommodation);
                roomDao.update(room);
            }
        }
    }

    @Transactional
    public boolean isFreeAtDate(Room room, LocalDate dateStart){

        for (Accommodation accomodation: room.getActualAccommodations()) {
            if (accomodation.getDateEnd().isBefore(dateStart)) return true;
            else return false;
        }
        return false;
    }


}
