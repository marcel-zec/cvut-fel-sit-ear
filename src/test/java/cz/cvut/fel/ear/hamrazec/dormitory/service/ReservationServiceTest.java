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

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class ReservationServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ReservationService reservationService;

    private Student student, student1;
    private Room room;
    private Reservation reservation;
    private Reservation reservation1;
    private Block block;

    @Before
    public void before() {
        reservation = new Reservation();
        reservation.setDateEnd(LocalDate.parse("2022-12-03"));
        reservation.setDateStart(LocalDate.parse("2021-12-03"));

        reservation1 = new Reservation();
        reservation1.setDateStart(LocalDate.now());
        reservation1.setDateEnd(LocalDate.parse("2022-12-03"));

        student = new Student();
        student = Generator.generateStudent();
        em.persist(student);

        student1 = new Student();
        student1 = Generator.generateStudent();
        em.persist(student1);

        block = new Block();
        block.setName("6");
        block.setFloors(4);
        block.setAddress("OLYMPIJSKA");


        room = new Room();
        room.setRoomNumber(234);
        room.setFloor(2);
        room.setMaxCapacity(4);
        room.setBlock(block);

        block.addRoom(room);
        em.persist(block);
        em.persist(room);
    }

    @Test
    public void createReservation_normalEntry_worksCorrect() throws NotFoundException, NotAllowedException {
        reservationService.createNewReservation(reservation,student.getId(),block.getName(), room.getRoomNumber());
        assertEquals("Student has not new reservation.", reservation , em.find(Student.class,student.getId())
                .getReservation());
    }

    @Test
    public void createReservation_studentHasAlreadyReservation_throwException() throws NotFoundException, NotAllowedException {

        thrown.expect(NotAllowedException.class);
        thrown.reportMissingExceptionWithMessage("Trying create reservation, but student already has existing reservation.");
        reservationService.createNewReservation(reservation, student.getId(),block.getName(), room.getRoomNumber());
        reservationService.createNewReservation(reservation,student.getId(),block.getName(),  room.getRoomNumber());
    }

    @Test
    public void createReservation_studentNotExist_throwException() throws NotFoundException, NotAllowedException {

        thrown.expect(NotFoundException.class);
        thrown.reportMissingExceptionWithMessage("Trying create reservation to not existing student.");
        reservationService.createNewReservation(reservation, (long) 4,block.getName(), room.getRoomNumber());
    }

    @Test
    public void createReservation_roomNotExist_throwException() throws NotFoundException, NotAllowedException {

        thrown.expect(NotFoundException.class);
        thrown.reportMissingExceptionWithMessage("Trying create reservation to not existing room.");
        reservationService.createNewReservation(reservation, student.getId(),block.getName (), 551);
    }

    @Test
    public void createNewReservationRandom_normalEntry_worksCorrect() throws NotFoundException, NotAllowedException {

        reservationService.createNewReservationRandom(reservation,student.getId(),block.getName());
        assertEquals("Student has not new reservation.", reservation , em.find(Student.class,student.getId())
                .getReservation());
    }
}
