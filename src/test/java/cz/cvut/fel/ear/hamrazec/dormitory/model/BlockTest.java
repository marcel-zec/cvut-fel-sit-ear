package cz.cvut.fel.ear.hamrazec.dormitory.model;

import cz.cvut.fel.ear.hamrazec.dormitory.exception.AlreadyExistsException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BlockTest {
    Block block;

    @Before
    public void init(){
        this.block = new Block();
    }

    @Test
    public void addRoomToBlock_WorksCorrect(){
        block.addRoom(new Room());
        assertEquals("Add room not working",1,block.getRooms().size());
    }

    @Test
    public void addManagerToBlock_WorksCorrect() throws AlreadyExistsException {
        block.addManager(new Manager());
        assertEquals("Add manager not working",1,block.getManagers().size());
    }

    @Test
    public void addManagerAlreadyAdded_NothingHappen() throws AlreadyExistsException {
        Manager manager = new Manager();
        block.addManager(manager);
        block.addManager(manager);
        assertEquals("Added manager two times",1,block.getManagers().size());
    }

    @Test
    public void addRoomAlreadyAdded_NothingHappen(){
        Room room = new Room();
        block.addRoom(room);
        block.addRoom(room);
        assertEquals("Added room two times",1,block.getRooms().size());
    }
}
