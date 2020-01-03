package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.*;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotAllowedException;
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

    public List<Accommodation> findAll(Long student_id) {
        List<Accommodation> accommodationsByStudent = new ArrayList<>();
        for (Accommodation a: findAll()) {
            if (a.getStudent().getId().equals(student_id)) accommodationsByStudent.add(a);
        }
        return accommodationsByStudent;
    }

    public List<Accommodation> findAll() {
        return acoDao.findAll();
    }

    public Accommodation find(Long id) {
        return acoDao.find(id);
    }


    @Transactional
    public void create(Accommodation accommodation, Long student_id, Long room_id) throws NotFoundException, NotAllowedException {

        if (!accommodation.getDateStart().equals(LocalDate.now())) {
            throw new NotAllowedException("bad date");
        }
        Student student = studentDao.find(student_id);
        Room room = roomDao.find(room_id);
        accommodation.setStudent(student);
        accommodation.setRoom(room);
        accommodation.setStatus(Status.ACC_ACTIVE);
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
    public void createFromReservation(Reservation reservation) throws NotFoundException, NotAllowedException {

        Room room;
        Student student = reservation.getStudent();
        if (student == null) throw new NotFoundException();

        if (reservation.getDateStart().equals(LocalDate.now())) {
            room = roomService.removeActualReservationStart(reservation);
            Accommodation accommodation = newAccomodationFromReservation(reservation);
            room.addActualAccomodation(accommodation);
            student.addAccommodation(accommodation);

            roomDao.update(room);
            studentDao.update(student);
            reservationDao.remove(reservation);
        }else throw new NotAllowedException("Bad date");
    }

    private Accommodation newAccomodationFromReservation(Reservation reservation){
        Accommodation accommodation = new Accommodation();
        accommodation.setStudent(reservation.getStudent());
        accommodation.setRoom(reservation.getRoom());
        accommodation.setDateEnd(reservation.getDateEnd());
        accommodation.setDateStart(reservation.getDateStart());
        accommodation.setStatus(Status.ACC_ACTIVE);
        acoDao.persist(accommodation);
        return accommodation;
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

    @Transactional
    public void createNewAccommodationRandom(Accommodation accommodation, Long student_id, String blockName) throws NotFoundException, NotAllowedException {

        Student student = studentDao.find(student_id);
        accommodation.setStudent(student);
        if (student == null) throw new NotFoundException();

        if (accommodation.getDateStart().equals(LocalDate.now())) {
            List<Room> freeRooms = roomService.findFreeRooms(blockName, accommodation.getDateStart(), accommodation.getDateEnd());
            if (freeRooms != null) {
                accommodation.setStatus(Status.ACC_ACTIVE);
                Room room = freeRooms.get(0);
                accommodation.setRoom(room);
                room.addActualAccomodation(accommodation);
                student.addAccommodation(accommodation);
                acoDao.persist(accommodation);
                studentDao.update(student);
                roomDao.update(room);
            }
        } else throw new NotAllowedException("Bad date");
    }

    @Transactional
    public void delete(Long id) throws NotFoundException {
        Accommodation accommodation = acoDao.find(id);
        if (accommodation == null) throw new NotFoundException();
        cancelAccommodation(accommodation);
    }


    @Transactional
    public void cancelAccommodation(Accommodation accommodation) {
        setStatusAndUnusualEnd(accommodation,Status.ACC_CANCELED);
        roomService.removeEndedActualAccommodation(accommodation.getRoom());
        //TODO - presun
    }


    private void setStatusAndUnusualEnd(Accommodation accommodation,Status status){
        accommodation.setStatus(status);
        accommodation.setDateUnusualEnd(LocalDate.now());
        acoDao.update(accommodation);
    }
}
