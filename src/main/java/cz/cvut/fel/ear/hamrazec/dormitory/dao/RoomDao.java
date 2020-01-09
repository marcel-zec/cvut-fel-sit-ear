package cz.cvut.fel.ear.hamrazec.dormitory.dao;

import cz.cvut.fel.ear.hamrazec.dormitory.model.Reservation;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Room;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.util.Objects;

@Repository
public class RoomDao extends BaseDao<Room> {
    public RoomDao(){super(Room.class);}

    public Room find(String blockName, Integer roomNumber) {
        try {
            return em.createNamedQuery("Room.findByBlockName", Room.class).setParameter("blockname", blockName)
                    .setParameter("roomNumber", roomNumber)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
