package cz.cvut.fel.ear.hamrazec.dormitory.model;

import cz.cvut.fel.ear.hamrazec.dormitory.exception.AlreadyExistsException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class BlockTest {
    Block block;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void init(){
        this.block = new Block();
    }

    @Test
    public void addRoomToBlock_WorksCorrect() throws AlreadyExistsException {
        block.addRoom(new Room());
        assertEquals("Add room not working",1,block.getRooms().size());
    }

    @Test
    public void addManagerToBlock_WorksCorrect() throws AlreadyExistsException {
        block.addManager(new Manager());
        assertEquals("Add manager not working",1,block.getManagers().size());
    }

    @Test
    public void addManagerAlreadyAdded_AlreadyExistException() throws AlreadyExistsException {
        thrown.expect(AlreadyExistsException.class);

        Manager manager = new Manager();
        block.addManager(manager);
        block.addManager(manager);
    }

    @Test
    public void addRoomAlreadyAdded_AlreadyExistException() throws AlreadyExistsException {
        thrown.expect(AlreadyExistsException.class);

        Room room = new Room();
        block.addRoom(room);
        block.addRoom(room);
    }
}
