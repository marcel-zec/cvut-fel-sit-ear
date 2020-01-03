package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.BlockDao;
import cz.cvut.fel.ear.hamrazec.dormitory.dao.RoomDao;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.*;
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


    @Transactional
    public List<Room> findFreeRooms(String blockName, LocalDate dateStart, LocalDate dateEnd) throws NotFoundException {

        Block block = blockDao.find(blockName); //TODO najdi podla mena nie podla id
        if (block == null) throw new NotFoundException();
        for (Room room: block.getRooms()) {
            removeEndedActualAccommodation(room);

            int freeAcco = freePlacesAtDateAccomodation(room,dateStart);
            int reservationsAtDate = reservationPlacesAtDateReserve(room,dateStart,dateEnd);

            if (room.getActualAccommodations().size() < room.getMaxCapacity()) {
                freeAcco = freeAcco + (room.getMaxCapacity() - room.getActualAccommodations().size());
            }

            if (reservationsAtDate < freeAcco) {
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
    public boolean findFreeConcreteRoom(String blockName,  LocalDate dateStart, LocalDate dateEnd, Integer roomnumber) throws NotFoundException {

        List<Room> rooms = findFreeRooms(blockName,dateStart,dateEnd);
        for (Room room: rooms) {
            if (roomnumber.equals(room.getRoomNumber())) return true;
        }
        return false;
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
            if (accommodation.getStatus() == Status.ACC_ENDED) {
                room.addPastAccomodation(accommodation);
                room.cancelActualAccomodation(accommodation);
                roomDao.update(room);
            }
        }
    }

    @Transactional
    public void removeEndedActualReservation(Room room, Student student){

        for (Reservation reservation: room.getReservations()) {
            if (reservation.getStatus() == Status.RES_APPROVED && reservation.getStudent().equals(student)) {
                room.cancelActualReservation(reservation);
                roomDao.update(room);
            }
        }
    }

    @Transactional
    public int freePlacesAtDateAccomodation(Room room, LocalDate dateStart){
        int endedAcco = 0;

        for (Accommodation accomodation: room.getActualAccommodations()) {
            if (accomodation.getDateEnd().isBefore(dateStart)) endedAcco++;
        }
        return endedAcco;
    }

    @Transactional
    public int reservationPlacesAtDateReserve(Room room, LocalDate dateStart, LocalDate dateEnd){
        int reservePlaces = 0;

        for (Reservation reservation: room.getReservations()) {
            if (reservation.getDateEnd().isBefore(dateStart) || reservation.getDateStart().isAfter(dateEnd)) {
            }
            else reservePlaces++;
        }
        return reservePlaces;
    }


}
