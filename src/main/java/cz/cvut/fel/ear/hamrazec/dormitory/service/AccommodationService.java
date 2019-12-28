package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.*;
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
public class AccommodationService {

    private AccommodationDao acoDao;
    private StudentDao studentDao;
    private RoomService roomService;
    private RoomDao roomDao;
    private ReservationDao reservationDao;


    @Autowired
    public AccommodationService(AccommodationDao acoDao, StudentDao studentDao, RoomService roomService, RoomDao roomDao,
                                ReservationDao reservationDao) {

        this.acoDao = acoDao;
        this.studentDao = studentDao;
        this.roomDao = roomDao;
        this.roomService = roomService;
        this.reservationDao = reservationDao;

    }

    public List<Accommodation> findAll(String blockName) {

        List<Accommodation> accommodationsByBlock = new ArrayList<>();
        for (Accommodation a: findAll()) {
            if (a.getRoom().getBlock().getName().equals(blockName)) accommodationsByBlock.add(a);
        }
        return accommodationsByBlock;
    }

    public List<Accommodation> findAll() {
        return acoDao.findAll();
    }

    public Accommodation find(Long id) {
        return acoDao.find(id);
    }


    @Transactional
    public void create(Accommodation accommodation, Long student_id, Long room_id) throws NotFoundException {

        Student student = studentDao.find(student_id);
        Room room = roomDao.find(room_id);
        accommodation.setStudent(student);
        accommodation.setRoom(room);
        Reservation reservation = roomService.getReservation(room,student);

        if (student == null || room == null) throw new NotFoundException();

        if (reservation != null && reservation.getDateStart().equals(LocalDate.now())) {
            createFromReservation(reservation);
            return;
        }

        if (reservation == null || !reservation.getDateStart().equals(LocalDate.now())){
            if (roomService.findFreeConcreteRoom(accommodation.getRoom().getBlock().getName(),accommodation.getDateStart(),
                    accommodation.getDateEnd(),accommodation.getRoom().getRoomNumber()))
            {
                room.addActualAccomodation(accommodation);
                student.addAccommodation(accommodation);
                acoDao.persist(accommodation);
                roomDao.update(room);
                studentDao.update(student);
            }
        }
    }

    @Transactional
    public void createFromReservation(Reservation reservation) throws NotFoundException {

        Room room;
        Student student = reservation.getStudent();
        if (student == null) throw new NotFoundException();

        if (reservation.getDateStart().equals(LocalDate.now())) {
            room = roomService.removeActualReservationStart(reservation);
            room.addActualAccomodation((Accommodation) reservation);
            student.addAccommodation((Accommodation) reservation);

            acoDao.persist((Accommodation) reservation);
            roomDao.update(room);
            studentDao.update(student);
            reservationDao.remove(reservation);
        }
    }

    @Scheduled(cron = "0 0 3 * * *", zone = "CET")
    @Transactional
    public void updateExpired(){

        for (Accommodation accommodation: findAll()) {
            if (accommodation.getDateEnd().isBefore(LocalDate.now())) {
                accommodation.setStatus(Status.ACC_ENDED);
                acoDao.update(accommodation);
            }
        }

    }

    //TODO - create na náhodnu izbu

    @Transactional
    public void delete(Long id) throws NotFoundException {
        Accommodation accommodation = acoDao.find(id);
        if (accommodation == null) throw new NotFoundException();
        cancelAccommodation(accommodation);
    }


    @Transactional
    public void cancelAccommodation(Accommodation accommodation) {
        setStatusAndUnusualEnd(accommodation,Status.ACC_CANCELED);
        accommodation.getRoom().cancelActualAccomodation(accommodation);
        accommodation.getRoom().addPastAccomodation(accommodation);
    }


    private void setStatusAndUnusualEnd(Accommodation accommodation,Status status){
        accommodation.setStatus(status);
        accommodation.setDateUnusualEnd(LocalDate.now());
        acoDao.update(accommodation);
    }
}
