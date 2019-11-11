package cz.cvut.fel.ear.hamrazec.dormitory.dao;

import cz.cvut.fel.ear.hamrazec.dormitory.model.Room;
import org.springframework.stereotype.Repository;

@Repository
public class RoomDao extends BaseDao<Room> {
    public RoomDao(){super(Room.class);}
}
