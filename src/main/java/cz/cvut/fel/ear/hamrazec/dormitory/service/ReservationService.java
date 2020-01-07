package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.BlockDao;
import cz.cvut.fel.ear.hamrazec.dormitory.dao.ReservationDao;
import cz.cvut.fel.ear.hamrazec.dormitory.dao.RoomDao;
import cz.cvut.fel.ear.hamrazec.dormitory.dao.StudentDao;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.EndOfStudyExpirationException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotAllowedException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.*;
import cz.cvut.fel.ear.hamrazec.dormitory.security.SecurityUtils;
import cz.cvut.fel.ear.hamrazec.dormitory.service.security.AccessService;
import cz.cvut.fel.ear.hamrazec.dormitory.service.security.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private ReservationDao reservationDao;
    private StudentDao studentDao;
    private RoomDao roomDao;
    private RoomService roomService;
    private BlockDao blockDao;
    private AccessService accessService;
    private RoleService roleService;

    @Autowired
    public ReservationService(ReservationDao reservationDao, StudentDao studentDao, RoomDao roomDao, RoomService roomService, BlockDao blockDao, AccessService accessService, RoleService roleService) {

        this.reservationDao = reservationDao;
        this.studentDao = studentDao;
        this.roomDao = roomDao;
        this.roomService = roomService;
        this.blockDao = blockDao;
        this.accessService = accessService;
        this.roleService = roleService;
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
    public void cancelReservation(Long id) throws NotFoundException {
        Reservation reservation = reservationDao.find(id);
        if (reservation == null) throw new NotFoundException();
        cancelReservation(reservation);
    }

    @Transactional
    public void createNewReservation(Reservation reservation, String block_name, int room_number) throws EndOfStudyExpirationException, NotFoundException, NotAllowedException {
        long id = SecurityUtils.getCurrentUser().getId();
        createNewReservation(reservation,id,block_name,room_number);
    }

    @Transactional
    public void createNewReservation(Reservation reservation, Long student_id, String block_name, int room_number) throws NotFoundException, NotAllowedException, EndOfStudyExpirationException {

        if (block_name == null || reservation == null || student_id==null) throw new NotFoundException();

        Student student = studentDao.find(student_id);
        Block block = blockDao.find(block_name);
        accessService.managerAccess(block);
        accessService.studentAccess(student_id);

        if (reservation.getDateEnd().isAfter(student.getEndOfStudy())) throw new EndOfStudyExpirationException(student.getEndOfStudy(),reservation.getDateEnd());
        if (student.hasActiveAccommodation()){
            AtomicBoolean dateIsOkay = new AtomicBoolean(true);
            student.getAccommodations().stream()
                    .filter(accommodation -> accommodation.getStatus().equals(Status.ACC_ACTIVE))
                    .findAny()
                    .ifPresent(accommodation -> dateIsOkay.set(accommodation.getDateEnd().isBefore(reservation.getDateStart())));
            if (!dateIsOkay.get()) throw new NotAllowedException();
        }

        List<Room> roomList = block.getRooms().stream().filter(room1 -> room1.getRoomNumber().equals(room_number)).collect(Collectors.toList());

        if (roomList.size() == 0) throw new NotFoundException();
        Room room = roomList.get(0);

        if (room == null) throw new NotFoundException();
        if (student.getReservation() != null){
            if (student.getReservation().getStatus().equals(Status.RES_CANCELED)){
                Room roomForUpdate = student.getReservation().getRoom();
                roomForUpdate.getReservations().remove(student.getReservation());
                roomDao.update(room);
                reservationDao.remove(student.getReservation());
                student.setReservation(null);
                studentDao.update(student);
            } else {
                throw new NotAllowedException("Reservation already exist.");
            }
        }
        reservation.setStudent(student);
        reservation.setRoom(room);

        if (roomService.findFreeConcreteRoom(room.getBlock().getName(),reservation.getDateStart(),
                reservation.getDateEnd(),reservation.getRoom().getRoomNumber()))
        {
            if (roleService.isManager(SecurityUtils.getCurrentUser())) reservation.setStatus(Status.RES_APPROVED);
            else reservation.setStatus(Status.RES_PENDING);
            room.addReservation(reservation);
            student.setReservation(reservation);
            reservationDao.persist(reservation);
            studentDao.update(student);
            roomDao.update(room);
        }

    }

    @Transactional
    public void approveReservation(Long reservationId) throws NotFoundException, NotAllowedException {
        Reservation reservation = reservationDao.find(reservationId);
        if (reservation == null) throw new NotFoundException();
        accessService.managerAccess(reservation.getRoom().getBlock());
        if (reservation.getStatus().equals(Status.RES_PENDING)){
            reservation.setStatus(Status.RES_APPROVED);
            reservationDao.update(reservation);
        }
    }

    @Transactional
    public void createNewReservationRandom(Reservation reservation, String blockName) throws NotFoundException, NotAllowedException, EndOfStudyExpirationException {
        long id = SecurityUtils.getCurrentUser().getId();
        createNewReservationRandom(reservation,id,blockName);
    }

    @Transactional
    public void createNewReservationRandom(Reservation reservation, long student_id, String blockName) throws NotFoundException, NotAllowedException, EndOfStudyExpirationException {

        accessService.studentAccess(student_id);
        accessService.managerAccess(blockDao.find(blockName));

        List<Room> freeRooms = roomService.findFreeRooms(blockName, reservation.getDateStart(), reservation.getDateEnd());
        if (freeRooms != null){
            Room room = freeRooms.get(0);
            createNewReservation(reservation,student_id,room.getBlock().getName(),room.getRoomNumber());
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

    @Transactional
    public void deleteReservation(Reservation reservation) throws NotFoundException, NotAllowedException {
        accessService.managerAccess(reservation.getRoom().getBlock());
        if (reservation.getStudent() != null)  reservation.getStudent().cancelReservation(reservation);
        if (reservation.getRoom() != null) reservation.getRoom().cancelActualReservation(reservation);
        studentDao.update(reservation.getStudent());
        roomDao.update(reservation.getRoom());
        reservationDao.remove(reservation);
    }

    @Transactional
    public void deleteReservation(Long reservation_id) throws NotFoundException, NotAllowedException {
        Reservation reservation = reservationDao.find(reservation_id);
        if (reservation == null) throw new NotAllowedException();
        deleteReservation(reservation);
    }

    @Transactional
    public void deleteReservation() throws NotFoundException, NotAllowedException {
        User user = SecurityUtils.getCurrentUser();
        if (roleService.isStudent(user)){
            Student student = studentDao.find(user.getId());
            Reservation reservation = student.getReservation();
            if (reservation != null) deleteReservation(reservation);
            else throw new NotFoundException();
        }
    }
}
