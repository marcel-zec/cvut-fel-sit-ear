package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.ReservationDao;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Accommodation;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Reservation;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {

    private ReservationDao reservationDao;
    private AccommodationService accommodationService;

    @Autowired
    public ReservationService(ReservationDao reservationDao, AccommodationService accommodationService) {

        this.reservationDao = reservationDao;
        this.accommodationService = accommodationService;
    }

    public List<Reservation> findAll() { return reservationDao.findAll();  }

    public Reservation find(Long id) {
        return reservationDao.find(id);
    }

    @Transactional
    public void cancelReservation(Reservation reservation) {
        setStatusAndUnusualEnd(reservation,Status.RES_CANCELED);
    }

    //TODO - reserve na konkretnu izbu a nahodnu izbu

    @Scheduled(cron = "0 0 3 * * *", zone = "CET")
    @Transactional
    public void updateExpired() {

        //TODO - reservation
    }

    private void setStatusAndUnusualEnd(Reservation reservation, Status status){
        reservation.setStatus(status);
        reservation.setDateUnusualEnd(LocalDate.now());
        reservationDao.update(reservation);
    }
}
