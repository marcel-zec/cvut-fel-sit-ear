package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.BlockDao;
import cz.cvut.fel.ear.hamrazec.dormitory.dao.ManagerDao;
import cz.cvut.fel.ear.hamrazec.dormitory.dao.RoomDao;
import cz.cvut.fel.ear.hamrazec.dormitory.dao.StudentDao;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.AlreadyExistsException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.BadFloorException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.*;
import cz.cvut.fel.ear.hamrazec.dormitory.rest.RoomController;
import javassist.bytecode.analysis.ControlFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RoomService {

    private static final Logger LOG = LoggerFactory.getLogger(RoomService.class);
    private final BlockDao blockDao;
    private final RoomDao roomDao;
    private StudentDao studentDao;
    private AccommodationService accommodationService;


    @Autowired
    public RoomService(BlockDao blockDao, RoomDao roomDao, StudentDao studentDao, AccommodationService accommodationService) {

        this.blockDao = blockDao;
        this.roomDao = roomDao;
        this.studentDao = studentDao;
        this.accommodationService = accommodationService;
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
    public boolean findFreeConcreteRoom(String blockName,  LocalDate dateStart, LocalDate dateEnd, Integer roomNumber) throws NotFoundException {

        List<Room> rooms = findFreeRooms(blockName, dateStart, dateEnd);
        for (Room room: rooms) {
            if (roomNumber.equals(room.getRoomNumber())) return true;
        }
        return false;
    }

    @Transactional
    public void addRoom(String blockName, Room room) throws NotFoundException, AlreadyExistsException, BadFloorException {

        Block block = blockDao.find(blockName);
        List<Room> rooms = findAll(blockName);
        if (block == null || room == null) throw new NotFoundException();

        if (room.getFloor() > block.getFloors() || room.getFloor() < 0) {
            throw new BadFloorException("Block is " + block.getFloors() + " floors high. Set floor between zero and " + block.getFloors());
        }

                room.setBlock(block);

        boolean roomExist = rooms.stream()
                .filter(findingRoom -> findingRoom.getFloor().equals(room.getFloor()))
                .anyMatch(findingRoom -> findingRoom.getRoomNumber().equals(room.getRoomNumber()));


        if(!roomExist) {
            roomDao.persist(room);
            block.addRoom(room);
            blockDao.update(block);
        } else {
            LOG.error("Room number "+ room.getRoomNumber() + " at floor " + room.getFloor() + " at block " + block.getName() + " already exists." );
            throw new AlreadyExistsException();
        }
    }

    @Transactional
    public void removeEndedActualAccommodation(Room room){
        List<Accommodation> actual = room.getActualAccommodations();
        for (int i = 0; i < actual.size(); i++) {
            Accommodation a = actual.get(i);
            if (a.getStatus() == Status.ACC_ENDED || a.getStatus() == Status.ACC_CANCELED) {
                room.addPastAccomodation(a);
                room.cancelActualAccomodation(a);
            }
        }
        roomDao.update(room);
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

    @Transactional
    public void deleteRoom(Room room){
        room.getActualAccommodations().stream().forEach(accommodation -> {
            try {
                accommodationService.delete(accommodation.getId());
            } catch (NotFoundException e) {
                LOG.error("Bad list of accommodations in room with id: " + room.getId());
            }
        });
    }


}
