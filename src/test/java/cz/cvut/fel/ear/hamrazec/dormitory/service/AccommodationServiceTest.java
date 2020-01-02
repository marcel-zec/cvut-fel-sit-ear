package cz.cvut.fel.ear.hamrazec.dormitory.service;

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
public class AccommodationServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private AccommodationService accommodationService;

    private Accommodation accommodation;
    private Student student;
    private Room room;
    private Reservation reservation;
    private Reservation reservation1;
    private  Block block;


    @Before
    public void before() {
        accommodation = new Accommodation();
        accommodation.setDateEnd(LocalDate.parse("2022-12-03"));
        accommodation.setDateStart(LocalDate.now());

        reservation = new Reservation();
        reservation.setDateEnd(LocalDate.parse("2022-12-03"));
        reservation.setDateStart(LocalDate.parse("2021-12-03"));

        reservation1 = new Reservation();
        reservation1.setDateStart(LocalDate.now());
        reservation1.setDateEnd(LocalDate.parse("2022-12-03"));

        student = new Student();
        student.setGender(Gender.MAN);
        student.setBankAccountNumber("AB12345678912345");
        student.setBirth(LocalDate.parse("2007-12-03"));
        student.setEndOfStudy(LocalDate.parse("2022-12-03"));
        student.setUniversity("CVUT");
        student.setEmail("testik@test.com");
        student.setFirstName("jozko");
        student.setUsername("testusername");
        student.setLastName("mrkva");
        student.setPassword("fefebdfbzzz");
        student.setRole(Role.STUDENT);
        em.persist(student);

        block = new Block();
        block.setName("6");
        block.setAddress("OLYMPIJSKA");


        room = new Room();
        room.setRoomNumber(234);
        room.setFloor(2);
        room.setMaxCapacity(4);
        room.setBlock(block);

        block.addRoom(room);
        reservation.setRoom(room);
        reservation.setStudent(student);
        reservation1.setRoom(room);
        reservation1.setStudent(student);

        em.persist(block);
        em.persist(room);
        em.persist(reservation);
        em.persist(reservation1);
    }

    @Test
    public void createAccommodation() throws NotFoundException, NotAllowedException {

        accommodationService.create(accommodation, student.getId(), room.getId());
        assertEquals("Student has not new accommodation.", 1 , em.find(Student.class,student.getId())
                .getAccommodations().size());
    }

    @Test
    public void createAccommodationWithNoExistingStudent() throws NotFoundException, NotAllowedException {

        thrown.expect(NotFoundException.class);
        thrown.reportMissingExceptionWithMessage("Trying create accommodation to not existing student.");
        accommodationService.create(accommodation, (long)4,room.getId());
    }

    @Test
    public void createAccommodationWithNoExistingRoom() throws NotFoundException, NotAllowedException {

        thrown.expect(NotFoundException.class);
        thrown.reportMissingExceptionWithMessage("Trying create accommodation to not existing room.");
        accommodationService.create(accommodation,student.getId(), (long)222);
    }

    @Test
    public void createAccommodationFromReservation_badDate() throws NotFoundException, NotAllowedException {

        thrown.expect(NotAllowedException.class);
        thrown.reportMissingExceptionWithMessage("Trying create accommodation with bad date.");
        accommodationService.createFromReservation(reservation);
    }

    @Test
    public void createAccommodationFromReservation() throws NotFoundException, NotAllowedException {

        accommodationService.createFromReservation(reservation1);
        assertEquals("Student has not new accommodation.", 1 , em.find(Student.class,student.getId())
                .getAccommodations().size());
    }

    @Test
    public void createAccommodationRandom() throws NotFoundException, NotAllowedException {

        accommodationService.createNewAccommodationRandom(accommodation,student.getId(),block.getName());
        assertEquals("Student has not new accommodation.", 1 , em.find(Student.class,student.getId())
                .getAccommodations().size());
    }

}
