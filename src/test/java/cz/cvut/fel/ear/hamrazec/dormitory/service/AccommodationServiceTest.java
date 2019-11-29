package cz.cvut.fel.ear.hamrazec.dormitory.service;

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

    @Before
    public void before() {
        accommodation = new Accommodation();
        accommodation.setDateEnd(LocalDate.parse("2022-12-03"));
        accommodation.setDateStart(LocalDate.now());

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
    }

    @Test
    //neni hotove
    public void createAccommodation() throws NotFoundException {

        accommodationService.create(accommodation,student.getId());
        assertEquals("Student has not new accommodation.", 1 , em.find(Student.class,student.getId())
                .getAccommodations().size());
    }

    @Test
    //neni hotove
    public void createAccommodationWithNoExistingStudent() throws NotFoundException {
        thrown.expect(NotFoundException.class);
        thrown.reportMissingExceptionWithMessage("Trying create accommodation to not existing student");
        accommodationService.create(accommodation, (long) 15);
    }
}
