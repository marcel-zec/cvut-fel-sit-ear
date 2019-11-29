package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Block;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Room;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class RoomServiceTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private RoomService roomService;

    private Block block;
    private Room room;

    @Before
    public void before() {
        block = new Block("Tst", "Testovacia adresa");
        em.persist(block);

        room = new Room();
    }

//    @Test
//    public void addRoomToBlock() throws NotFoundException {
//        room.setFloor(4);
//        room.setNumberOfPeople(2);
//        room.setRoomNumber(456);
//
//        roomService.addRoom(block.getName(),room);
//        assertEquals("Add room to block not working", 1 , em.find(Block.class,block.getId()).getRooms().size());
//    }
//
//
//    @Test
//    public void addRoomToNoneBlock() throws NotFoundException {
//        thrown.expect(NotFoundException.class);
//        thrown.reportMissingExceptionWithMessage("Trying add room to not existing block");
//        room.setFloor(4);
//        room.setNumberOfPeople(2);
//        room.setRoomNumber(456);
//
//        roomService.addRoom("Tes",room);
//    }

}
