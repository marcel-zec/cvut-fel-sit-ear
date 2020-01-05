package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.environment.Generator;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.AlreadyExistsException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotAllowedException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.*;
import cz.cvut.fel.ear.hamrazec.dormitory.security.SecurityUtils;
import cz.cvut.fel.ear.hamrazec.dormitory.security.model.UserDetails;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    private Room room;

    @Before
    public void before() throws AlreadyExistsException {
        block = Generator.generateBlockWithRooms();
        student = Generator.generateStudent();
        student.setGender(Gender.MAN);

        SuperUser superuser = new SuperUser();
        superuser.setUsername("superuser123");
        superuser.setEmail("milan@jano.cz");
        superuser.setFirstName("milan");
        superuser.setLastName("dyano");
        superuser.setPassword("dwfiv492925ov");
        superuser.setWorkerNumber(50);

        em.persist(superuser);
        Authentication auth = new UsernamePasswordAuthenticationToken(superuser.getUsername(), superuser.getPassword());
        UserDetails ud = new UserDetails(superuser);
        SecurityUtils.setCurrentUser(ud);

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

        List<Room> freeRooms = roomService.findFreeRooms(block.getName(),LocalDate.parse("2020-07-10"),LocalDate.parse("2021-08-20"));
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

        List<Room> freeRooms = roomService.findFreeRooms(block.getName(),LocalDate.parse("2020-07-10"),LocalDate.parse("2021-08-20"));
        assertEquals("findFreeRooms not working correctly",4,freeRooms.size());
    }

    @Test
    public void findFreeConcreteRoom_concreteFreeRoom_WorksCorrect() throws NotFoundException {
        Accommodation acco1 = Generator.generateActiveAccommodation(block.getRooms().get(3),
                student, LocalDate.now(), LocalDate.parse("2021-07-09"));
        em.persist(acco1);
        em.merge(block);

        boolean free = roomService.findFreeConcreteRoom(block.getName(), LocalDate.parse("2020-11-29"), LocalDate.parse("2021-07-09"), block.getRooms().get(3).getRoomNumber() );

        assertEquals("findFreeConcreteRooms not working correctly",true,free);
    }

    @Test
    public void findFreeConcreteRoom_notExistingRoom_WorksCorrect() throws NotFoundException {

        boolean free = roomService.findFreeConcreteRoom(block.getName(), LocalDate.parse("2020-08-29"), LocalDate.parse("2020-12-09"), 566 );
        assertEquals("findFreeConcreteRooms not working correctly",false,free);
    }

    @Test
    public void findFreeConcreteRoom_concreteNotFreeRoom_WorksCorrect() throws NotFoundException {
        Accommodation acco = Generator.generateActiveAccommodation(block.getRooms().get(0), student, LocalDate.parse("2020-11-29") , LocalDate.parse("2021-07-09"));
        em.persist(acco);
        em.merge(block);

        boolean free = roomService.findFreeConcreteRoom(block.getName(), LocalDate.parse("2020-11-29"), LocalDate.parse("2021-07-09"), block.getRooms().get(0).getRoomNumber() );
        assertEquals("findFreeConcreteRooms not working correctly",false,free);
    }


}
