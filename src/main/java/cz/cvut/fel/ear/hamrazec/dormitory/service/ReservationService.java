package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.BlockDao;
import cz.cvut.fel.ear.hamrazec.dormitory.dao.ReservationDao;
import cz.cvut.fel.ear.hamrazec.dormitory.dao.RoomDao;
import cz.cvut.fel.ear.hamrazec.dormitory.dao.StudentDao;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotAllowedException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.*;
import cz.cvut.fel.ear.hamrazec.dormitory.service.security.AccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private ReservationDao reservationDao;
    private StudentDao studentDao;
    private RoomDao roomDao;
    private RoomService roomService;
    private BlockDao blockDao;
    private AccessService accessService;

    @Autowired
    public ReservationService(ReservationDao reservationDao, StudentDao studentDao, RoomDao roomDao, RoomService roomService, BlockDao blockDao, AccessService accessService) {

        this.reservationDao = reservationDao;
        this.studentDao = studentDao;
        this.roomDao = roomDao;
        this.roomService = roomService;
        this.blockDao = blockDao;
        this.accessService = accessService;
    }

    public List<Reservation> findAll() { return reservationDao.findAll();  }

    public List<Reservation> findAll(String blockName) throws NotAllowedException, NotFoundException {
        accessService.managerAccess(blockDao.find(blockName));

        List<Reservation> reservationsInBlock = new ArrayList<>();
        for (Reservation reservation: findAll()) {
            if (reservation.getRoom().getBlock().getName().equals(blockName)) reservationsInBlock.add(reservation);
        }
        return reservationsInBlock;
    }

    public Reservation findbyStudent(Long student_id) throws NotAllowedException {

        accessService.studentAccess(student_id);
        for (Reservation r: findAll()) {
            if (r.getStudent().getId().equals(student_id)) return r;
        }
        return null;
    }

    public Reservation find(Long id) {
        return reservationDao.find(id);
    }

    @Transactional
    public void cancelReservation(Reservation reservation) {
        setStatusAndUnusualEnd(reservation,Status.RES_CANCELED);
    }

    @Transactional
    public void createNewReservation(Reservation reservation, Long student_id, String block_name, int room_number) throws NotFoundException, NotAllowedException {

        Student student = studentDao.find(student_id);
        Block block = blockDao.find(block_name);
        accessService.managerAccess(block);
        accessService.studentAccess(student_id);
        if (block_name == null) throw new NotFoundException();

        List<Room> roomList = block.getRooms().stream().filter(room1 -> room1.getRoomNumber().equals(room_number)).collect(Collectors.toList());

        if (roomList.size() == 0) throw new NotFoundException();
        Room room = roomList.get(0);

        if (student == null || room == null) throw new NotFoundException();
        if (student.getReservation() != null) throw new NotAllowedException("student already has reservation");
        reservation.setStudent(student);
        reservation.setRoom(room);

        if (roomService.findFreeConcreteRoom(room.getBlock().getName(),reservation.getDateStart(),
                reservation.getDateEnd(),reservation.getRoom().getRoomNumber()))
        {
            reservation.setStatus(Status.RES_APPROVED);
            room.addReservation(reservation);
            student.setReservation(reservation);
            reservationDao.persist(reservation);
            studentDao.update(student);
            roomDao.update(room);
        }

    }

    @Transactional
    public void createNewReservationRandom(Reservation reservation, long student_id, String blockName) throws NotFoundException, NotAllowedException {

        accessService.studentAccess(student_id);
        accessService.managerAccess(blockDao.find(blockName));
        Student student = studentDao.find(student_id);
        reservation.setStudent(student);
        if (student == null) throw new NotFoundException();

        List<Room> freeRooms = roomService.findFreeRooms(blockName, reservation.getDateStart(), reservation.getDateEnd());
        if (freeRooms !=null){
            reservation.setStatus(Status.RES_APPROVED);
            Room room = freeRooms.get(0);
            reservation.setRoom(room);
            room.addReservation(reservation);
            student.setReservation(reservation);
            reservationDao.persist(reservation);
            studentDao.update(student);
            roomDao.update(room);
        }
    }

    @Scheduled(cron = "0 0 3 * * *", zone = "CET")
    @Transactional
    public void updateExpired() {

        for (Reservation reservation: findAll()) {
            if (reservation.getDateEnd().isBefore(LocalDate.now())) {
                reservation.setStatus(Status.RES_CANCELED);
                reservationDao.update(reservation);
            }
        }
    }

    private void setStatusAndUnusualEnd(Reservation reservation, Status status){
        reservation.setStatus(status);
        reservation.setDateUnusualEnd(LocalDate.now());
        reservationDao.update(reservation);
    }

    public void deleteReservation(Reservation reservation){
        if (reservation.getStudent() != null)  reservation.getStudent().cancelReservation(reservation);
        if (reservation.getRoom() != null) reservation.getRoom().cancelActualReservation(reservation);
        studentDao.update(reservation.getStudent());
        roomDao.update(reservation.getRoom());
        reservationDao.remove(reservation);
    }
}
