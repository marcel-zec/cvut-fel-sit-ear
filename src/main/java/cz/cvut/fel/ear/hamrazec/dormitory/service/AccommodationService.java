package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.*;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.AlreadyExistsException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotAllowedException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.*;
import cz.cvut.fel.ear.hamrazec.dormitory.security.SecurityUtils;
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
public class AccommodationService {

    private AccommodationDao acoDao;
    private StudentDao studentDao;
    private RoomService roomService;
    private RoomDao roomDao;
    private ReservationDao reservationDao;
    private BlockDao blockDao;
    private ReservationService reservationService;
    private AccessService accessService;


    @Autowired
    public AccommodationService(AccommodationDao acoDao, StudentDao studentDao, RoomService roomService, RoomDao roomDao,
                                ReservationDao reservationDao, BlockDao blockDao, ReservationService reservationService, AccessService accessService) {

        this.acoDao = acoDao;
        this.studentDao = studentDao;
        this.roomDao = roomDao;
        this.roomService = roomService;
        this.reservationDao = reservationDao;
        this.blockDao = blockDao;
        this.reservationService = reservationService;
        this.accessService = accessService;

    }

    public List<Accommodation> findAll(String blockName) throws NotAllowedException, NotFoundException {
        List<Accommodation> accommodationsByBlock = new ArrayList<>();

        accessService.managerAccess(blockDao.find(blockName));
            for (Accommodation a: findAll()) {
            if (a.getRoom().getBlock().getName().equals(blockName)) accommodationsByBlock.add(a);
        }
        return accommodationsByBlock;
    }


    public List<Accommodation> findAll(Long student_id) throws NotAllowedException {
        List<Accommodation> accommodationsByStudent = new ArrayList<>();
        final User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser.getRole().equals(Role.STUDENT)) {
            if (!currentUser.getId().equals(student_id)) throw new NotAllowedException("Access denied.");
        }
        for (Accommodation a : findAll()) {
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

    public Accommodation findActualAccommodationOfStudent(Long student_id) throws NotAllowedException {
        final User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser.getRole().equals(Role.STUDENT)) {
            if (!currentUser.getId().equals(student_id)) throw new NotAllowedException("Access denied.");
            else {
                for (Accommodation accommodation:findAll(student_id) ) {
                    if (accommodation.getStatus() == Status.ACC_ACTIVE) return accommodation;
                }
            }
        }
        return null;
    }


    @Transactional
    public void create(Accommodation accommodation, Long student_id, int roomNumber, String blockName) throws NotFoundException, NotAllowedException, AlreadyExistsException {

        Student student = studentDao.find(student_id);
        Block block = blockDao.find(blockName);
        if (block == null || student==null) throw new NotFoundException();
        accessService.managerAccess(block);

        if (!accommodation.getDateStart().equals(LocalDate.now())) throw new NotAllowedException("bad date");

        if (studentDao.find(student_id).getAccommodations().stream()
                .anyMatch(acc -> acc.getStatus().equals(Status.ACC_ACTIVE))) throw new AlreadyExistsException();

        List<Room> roomList = block.getRooms().stream().filter(room1 -> room1.getRoomNumber().equals(roomNumber)).collect(Collectors.toList());

        if (roomList.size() == 0) throw new NotFoundException();

        Room room = roomList.get(0);
        accommodation.setStudent(student);
        accommodation.setRoom(room);
        accommodation.setStatus(Status.ACC_ACTIVE);
        Reservation reservation = roomService.getReservation(room,student);

        if (room == null) throw new NotFoundException();

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

                if (student.getReservation() !=null) reservationService.deleteReservation(student.getReservation());

                acoDao.persist(accommodation);
                roomDao.update(room);
                studentDao.update(student);
            }
        }
    }

    @Transactional
    public void createFromReservation(Reservation reservation) throws NotFoundException, NotAllowedException {

        if (reservation == null) throw new NotFoundException();
        accessService.managerAccess(reservation.getRoom().getBlock());
        Student student = reservation.getStudent();

        if (reservation.getDateStart().equals(LocalDate.now()) && reservation.getStatus().equals(Status.RES_APPROVED)) {
            Room room = roomService.removeActualReservationStart(reservation);
            Accommodation accommodation = newAccommodationFromReservation(reservation);
            room.addActualAccomodation(accommodation);
            student.addAccommodation(accommodation);

            roomDao.update(room);
            studentDao.update(student);
            reservationDao.remove(reservation);
        } else throw new NotAllowedException("Bad date or not approved reservation");
    }

    private Accommodation newAccommodationFromReservation(Reservation reservation){
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
        accessService.managerAccess(blockDao.find(blockName) );

        Student student = studentDao.find(student_id);
        accommodation.setStudent(student);
        if (student == null) throw new NotFoundException();

        if (student.getReservation() != null && student.getReservation().getDateStart().equals(LocalDate.now())) {
            if (student.getReservation().getRoom().getBlock().getName().equals(blockName)) createFromReservation(student.getReservation());
            else reservationDao.remove(student.getReservation());
        }

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
    }


    private void setStatusAndUnusualEnd(Accommodation accommodation,Status status){
        accommodation.setStatus(status);
        accommodation.setDateUnusualEnd(LocalDate.now());
        acoDao.update(accommodation);
    }


}
