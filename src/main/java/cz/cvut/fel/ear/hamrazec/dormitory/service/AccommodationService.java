package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.AccommodationDao;
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
public class AccommodationService {

    private AccommodationDao acoDao;
    private StudentDao studentDao;
    private RoomService roomService;
    private RoomDao roomDao;

    @Autowired
    public AccommodationService(AccommodationDao acoDao, StudentDao studentDao, RoomService roomService, RoomDao roomDao) {

        this.acoDao = acoDao;
        this.studentDao = studentDao;
        this.roomDao = roomDao;
        this.roomService = roomService;
    }

    public List<Accommodation> findAll() {
        return acoDao.findAll();
    }

    public Accommodation find(Long id) {
        return acoDao.find(id);
    }

    @Transactional
    public void create(Accommodation accommodation) throws NotFoundException {
        //TODO - pridat izbu, kontrolovat volnost izby v tom datume ,BLBOST lebo v rezervacii kontrolujeme volnost izby
        Student student = accommodation.getStudent();
        Room room = accommodation.getRoom();

        if (student == null) throw new NotFoundException();

        acoDao.persist(accommodation);
        room.addActualAccomodation(accommodation);
        roomService.removeEndedActualReservation(room,student);
        student.addAccommodation(accommodation);
        studentDao.update(student);
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

    //TODO - reserve na konkretnu izbu a nahodnu izbu


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

    @Transactional
    public void cancelReservation(Reservation reservation) {
        setStatusAndUnusualEnd(reservation,Status.RES_CANCELED);
    }

    private void setStatusAndUnusualEnd(Accommodation accommodation,Status status){
        accommodation.setStatus(status);
        accommodation.setDateUnusualEnd(LocalDate.now());
        acoDao.update(accommodation);
    }
}
