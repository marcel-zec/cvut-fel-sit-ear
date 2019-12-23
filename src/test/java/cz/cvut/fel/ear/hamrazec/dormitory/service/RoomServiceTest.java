package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.environment.Generator;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotAllowedException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.*;
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


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    private Student student;

    @Before
    public void before() {
        block = Generator.generateBlockWithRooms();
        student = Generator.generateStudent();

        em.persist(block);
        em.persist(student);
    }

    @Test
    public void findFreeRooms_3reservations_WorksCorrect() throws NotFoundException {
        Accommodation acco1 = Generator.generateActiveAccommodation(block.getRooms().get(3), student, LocalDate.parse("2019-11-29") , LocalDate.parse("2020-07-09"));
        em.persist(acco1);
        Accommodation acco2 = Generator.generateActiveAccommodation(block.getRooms().get(3), student, LocalDate.parse("2019-11-29") , LocalDate.parse("2021-08-20"));
        em.persist(acco2);
        Reservation res1 = Generator.generateReservation(block.getRooms().get(3),student, true, LocalDate.parse("2020-07-10") , LocalDate.parse("2020-08-20"));
        em.persist(res1);
        Reservation res2 = Generator.generateReservation(block.getRooms().get(3), student, true, LocalDate.parse("2020-07-10") , LocalDate.parse("2020-08-20"));
        em.persist(res2);
        Reservation res3 = Generator.generateReservation(block.getRooms().get(3), student , true, LocalDate.parse("2020-07-01") , LocalDate.parse("2020-08-29"));
        em.persist(res3);
        em.merge(block);

        List<Room> freeRooms = roomService.findFreeRooms(block.getName(),LocalDate.parse("2020-07-10"),LocalDate.parse("2020-08-20"));
        assertEquals("findFreeRooms not working correctly",3,freeRooms.size());
    }

    @Test
    public void findFreeRooms_2reservations_WorksCorrect() throws NotFoundException {
        Accommodation acco1 = Generator.generateActiveAccommodation(block.getRooms().get(3), student, LocalDate.parse("2019-11-29") , LocalDate.parse("2020-07-09"));
        em.persist(acco1);
        Accommodation acco2 = Generator.generateActiveAccommodation(block.getRooms().get(3), student,  LocalDate.parse("2019-11-29") , LocalDate.parse("2021-08-20"));
        em.persist(acco2);
        Reservation res1 = Generator.generateReservation(block.getRooms().get(3), student, true, LocalDate.parse("2020-07-10") , LocalDate.parse("2020-08-20"));
        em.persist(res1);
        Reservation res2 = Generator.generateReservation(block.getRooms().get(3),student,  true, LocalDate.parse("2020-07-10") , LocalDate.parse("2020-08-20"));
        em.persist(res2);
        em.merge(block);

        List<Room> freeRooms = roomService.findFreeRooms(block.getName(),LocalDate.parse("2020-07-10"),LocalDate.parse("2020-08-20"));
        assertEquals("findFreeRooms not working correctly",4,freeRooms.size());
    }

    @Test
    public void findFreeConcreteRoom_concreteFreeRoom_WorksCorrect() throws NotFoundException {
        Accommodation acco1 = Generator.generateActiveAccommodation(block.getRooms().get(3), student, LocalDate.parse("2019-11-29"), LocalDate.parse("2020-07-09"));
        em.persist(acco1);
        em.merge(block);

        boolean free = roomService.findFreeConcreteRoom(block.getName(), LocalDate.parse("2019-11-29"), LocalDate.parse("2020-07-09"), block.getRooms().get(3).getRoomNumber() );

        assertEquals("findFreeConcreteRooms not working correctly",true,free);
    }

    @Test
    public void findFreeConcreteRoom_notExistingRoom_WorksCorrect() throws NotFoundException {

        boolean free = roomService.findFreeConcreteRoom(block.getName(), LocalDate.parse("2019-11-29"), LocalDate.parse("2020-07-09"), 566 );
        assertEquals("findFreeConcreteRooms not working correctly",false,free);
    }

    @Test
    public void findFreeConcreteRoom_concreteNotFreeRoom_WorksCorrect() throws NotFoundException {
        Accommodation acco1 = Generator.generateActiveAccommodation(block.getRooms().get(0), student, LocalDate.parse("2019-11-29"), LocalDate.parse("2020-07-09"));
        em.persist(acco1);
        em.merge(block);

        boolean free = roomService.findFreeConcreteRoom(block.getName(), LocalDate.parse("2019-11-29"), LocalDate.parse("2020-07-09"), block.getRooms().get(0).getRoomNumber() );

        assertEquals("findFreeConcreteRooms not working correctly",false,free);
    }


//    @Test
//    public void addRoomToBlock() throws NotFoundException {
//
//        roomService.addRoom(block.getName(),block.getRooms().get(0));
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
