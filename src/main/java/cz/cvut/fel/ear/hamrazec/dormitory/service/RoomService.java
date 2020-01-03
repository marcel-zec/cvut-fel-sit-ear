package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.BlockDao;
import cz.cvut.fel.ear.hamrazec.dormitory.dao.ManagerDao;
import cz.cvut.fel.ear.hamrazec.dormitory.dao.RoomDao;
import cz.cvut.fel.ear.hamrazec.dormitory.dao.StudentDao;
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
    private StudentDao studentDao;


    @Autowired
    public RoomService(BlockDao blockDao, RoomDao roomDao, StudentDao studentDao) {

        this.blockDao = blockDao;
        this.roomDao = roomDao;
        this.studentDao = studentDao;
    }

    @Transactional
    public List<Room> findAll(String blockName) throws NotFoundException {
        Block block = blockDao.find(blockName);
        if (block == null) throw new NotFoundException();
        return block.getRooms();
    }

    @Transactional
    public List<Accommodation> getActualAccommodations(String blockName, Integer roomNumber){

       Room room = find(blockName,roomNumber);
       return room.getActualAccommodations();
    }

    //public Room findRoom(Long id){
    //    return roomDao.find(id);
   // }


    @Transactional
    public List<Room> findFreeRooms(String blockName, LocalDate dateStart, LocalDate dateEnd) throws NotFoundException {

        List<Room> roomList = new ArrayList<Room>();
        Block block = blockDao.find(blockName);
        if (block == null) throw new NotFoundException();
        if (block.getRooms() == null) throw new NotFoundException();

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


    public Room find(String blockName, Integer roomNumber) {
        return roomDao.find(blockName,roomNumber);
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
        List<Room> rooms = findAll(blockName);
        boolean roomExist = false;

        if (block == null || room == null) throw new NotFoundException();
        if (rooms != null) {
            for (Room r:rooms) {
                if (r == room){
                    roomExist = true;
                    break;
                }
            }
            if(!roomExist) roomDao.persist(room);
        }
        block.addRoom(room);
        blockDao.update(block);
    }

    @Transactional
    public void removeEndedActualAccommodation(Room room){

        if (room == null) return;
        if (room.getActualAccommodations() == null) return;
        List<Accommodation> accommodationforRemove = new ArrayList<>();

        for (int i = 0; i < room.getActualAccommodations().size(); i++) {
            if (room.getActualAccommodations().get(i).getStatus() == Status.ACC_ENDED) {
                room.addPastAccomodation(room.getActualAccommodations().get(i));
                accommodationforRemove.add(room.getActualAccommodations().get(i));
            }
        }


        for (Accommodation accommodation: room.getActualAccommodations()) {
            if (accommodationforRemove!=null ) room.cancelActualAccomodation(accommodationforRemove);
            if (accommodation.getStatus() == Status.ACC_ENDED) {
                room.addPastAccomodation(accommodation);
                accommodationforRemove = accommodation;
                roomDao.update(room);
            }
        }
    }

    @Transactional
    public Room removeActualReservationStart(Reservation reservation){

            if (reservation.getStatus() == Status.RES_APPROVED  && reservation.getDateStart().equals(LocalDate.now())) {
                reservation.getRoom().cancelActualReservation(reservation);
                reservation.getStudent().cancelReservation(reservation);
                roomDao.update(reservation.getRoom());
                studentDao.update(reservation.getStudent());
            }

        return roomDao.find(reservation.getRoom().getId());
    }

    @Transactional
    public Reservation getReservation(Room room, Student student) throws NotFoundException {

        if (room == null) throw new NotFoundException();
        if (room.getReservations() == null) return null;

        for (Reservation reservation: room.getReservations()) {
            if (reservation.getStatus() == Status.RES_APPROVED && reservation.getStudent().equals(student)) {
                return reservation;
            }
        }
        return null;
    }

    @Transactional
    public int freePlacesAtDateAccomodation(Room room, LocalDate dateStart) throws NotFoundException {
        int endedAcco = 0;

        if (room == null) throw new NotFoundException();

        for (Accommodation accomodation: room.getActualAccommodations()) {
            if (accomodation.getDateEnd().isBefore(dateStart)) endedAcco++;
        }
        return endedAcco;
    }

    @Transactional
    public int reservationPlacesAtDateReserve(Room room, LocalDate dateStart, LocalDate dateEnd) throws NotFoundException {
        int reservePlaces = 0;

        if (room == null ) throw new NotFoundException();
        for (Reservation reservation: room.getReservations()) {
            if (reservation.getDateEnd().isBefore(dateStart) || reservation.getDateStart().isAfter(dateEnd)) {
            }
            else reservePlaces++;
        }
        return reservePlaces;
    }


}
