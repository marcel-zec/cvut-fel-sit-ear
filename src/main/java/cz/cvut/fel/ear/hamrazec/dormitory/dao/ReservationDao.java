package cz.cvut.fel.ear.hamrazec.dormitory.dao;

import cz.cvut.fel.ear.hamrazec.dormitory.model.Block;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Reservation;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
public class ReservationDao extends BaseDao<Reservation> {
    public ReservationDao(){super(Reservation.class);}

}
