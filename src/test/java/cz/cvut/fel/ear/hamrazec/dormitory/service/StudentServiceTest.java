package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.environment.Generator;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotAllowedException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.*;
import org.junit.Before;
import org.junit.BeforeClass;
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
public class StudentServiceTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private StudentService studentService;

    Student firstStudent;
    static Block block;
    static Accommodation accommodation;
    static Student student;

    @Before
    public void before() {
        firstStudent = new Student();
        firstStudent.setFirstName("Test");
        firstStudent.setLastName("Test");
        firstStudent.setUsername("Test");
        firstStudent.setUniversity("CVUT");
        firstStudent.setBirth(LocalDate.of(1990,12,12));
        firstStudent.setPassword("heslotestovacie");
        firstStudent.setBankAccountNumber("AAAAAAAAAA2222222222");
        firstStudent.setEmail("test@test.test");
        firstStudent.setEndOfStudy(LocalDate.of(2040,12,12));
    }

    @BeforeClass
    public static void beforeClass(){
        block = Generator.generateBlockWithRooms();
        student = Generator.generateStudent();
        accommodation = Generator.generateActiveAccommodation(block.getRooms().get(0),student);
    }

    @Test
    public void addNewStudent_WorksCorrect() throws NotAllowedException {
        int numberBefore = studentService.findAll().size();
        studentService.create(firstStudent);

        assertEquals("Create student not working",numberBefore+1,studentService.findAll().size());
    }

    @Test
    public void addNewStudent_changedRole_ThrowNotAllowedException() throws NotAllowedException {
        thrown.expect(NotAllowedException.class);
        thrown.reportMissingExceptionWithMessage("Student with different role was created");

        firstStudent.setRole(Role.MANAGER);
        studentService.create(firstStudent);
    }

    @Test
    public void updateStudent_changedRole_ThrowNotAllowedException() throws NotFoundException, NotAllowedException {
        thrown.expect(NotAllowedException.class);
        thrown.reportMissingExceptionWithMessage("Student with different role was updated");
        studentService.create(firstStudent);

        firstStudent.setRole(Role.MANAGER);
        studentService.update(firstStudent.getId(),firstStudent);
    }

    @Test
    public void deleteStudent_studentWithoutAccommodation_WorksCorrectly() throws NotFoundException, NotAllowedException {
        studentService.create(firstStudent);
        int numberBefore = studentService.findAll().size();

        studentService.delete(firstStudent.getId());
        assertEquals("Delete without accommodation not working",numberBefore-1,studentService.findAll().size());
    }

    @Test
    public void deleteStudent_studentWithAccommodation_WorksCorrectly() throws NotFoundException, NotAllowedException {
        thrown.expect(NotAllowedException.class);
        thrown.reportMissingExceptionWithMessage("Student with active accommodation was deleted");
        studentService.create(firstStudent);
        firstStudent.addAccommodation(accommodation);

        studentService.delete(firstStudent.getId());
    }

}
