package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.ReservationDao;
import cz.cvut.fel.ear.hamrazec.dormitory.dao.RoomDao;
import cz.cvut.fel.ear.hamrazec.dormitory.dao.StudentDao;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationService {

    private ReservationDao reservationDao;
    private AccommodationService accommodationService;
    private StudentDao studentDao;
    private RoomDao roomDao;
    private RoomService roomService;

    @Autowired
    public ReservationService(ReservationDao reservationDao, AccommodationService accommodationService, StudentDao studentDao, RoomDao roomDao, RoomService roomService) {

        this.reservationDao = reservationDao;
        this.accommodationService = accommodationService;
        this.studentDao = studentDao;
        this.roomDao = roomDao;
        this.roomService = roomService;
    }

    public List<Reservation> findAll() { return reservationDao.findAll();  }

    public List<Reservation> findAll(String blockName) {

        List<Reservation> reservationsInBlock = new ArrayList<>();
        for (Reservation reservation: findAll()) {
            if (reservation.getRoom().getBlock().getName().equals(blockName)) reservationsInBlock.add(reservation);
        }
        return reservationsInBlock;
    }

    public Reservation findbyStudent(Long student_id) {

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
    public void createNewReservation(Reservation reservation, Long student_id, Long room_id) throws NotFoundException {

        Student student = studentDao.find(student_id);
        Room room = roomDao.find(room_id);
        reservation.setStudent(student);
        reservation.setRoom(room);
        if (student == null || room == null) throw new NotFoundException();

        if (roomService.findFreeConcreteRoom(room.getBlock().getName(),reservation.getDateStart(),
                reservation.getDateEnd(),reservation.getRoom().getRoomNumber()))
        {
            reservation.setStatus(Status.RES_APPROVED);
            room.addReservation(reservation);
            //student.addReservation(reservation);
            student.setReservation(reservation);
            reservationDao.persist(reservation);
            studentDao.update(student);
            roomDao.update(room);
        }

    }

    @Transactional
    public void createNewReservationRandom(Reservation reservation, Long student_id, String blockName) throws NotFoundException {

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
            //student.addReservation(reservation);
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
        reservationDao.remove(reservation);
    }
}
