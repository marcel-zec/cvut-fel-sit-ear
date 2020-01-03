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

    static Block block;
    static Accommodation accommodation;
    static Student student;

    @Before
    public void before() {
        student = Generator.generateStudent();
    }

    @BeforeClass
    public static void beforeClass(){
        block = Generator.generateBlockWithRooms();
        accommodation = Generator.generateActiveAccommodation(block.getRooms().get(0),student);
    }

    @Test
    public void addNewStudent_WorksCorrect() throws NotAllowedException {
        int numberBefore = studentService.findAll().size();
        studentService.create(student);

        assertEquals("Create student not working",numberBefore+1,studentService.findAll().size());
    }

    @Test
    public void deleteStudent_studentWithoutAccommodation_WorksCorrectly() throws NotFoundException, NotAllowedException {
        studentService.create(student);
        int numberBefore = studentService.findAll().size();

        studentService.delete(student.getId());
        assertEquals("Delete without accommodation not working",numberBefore-1,studentService.findAll().size());
    }

    @Test
    public void deleteStudent_studentWithAccommodation_WorksCorrectly() throws NotFoundException, NotAllowedException {
        thrown.expect(NotAllowedException.class);
        thrown.reportMissingExceptionWithMessage("Student with active accommodation was deleted");
        studentService.create(student);
        student.addAccommodation(accommodation);

        studentService.delete(student.getId());
    }

    @Test
    public void update_changeCorrect_worksCorrectly() throws NotAllowedException, NotFoundException {
        studentService.create(student);

        String name = "UpdatedName";
        student.setFirstName(name);
        studentService.update(student.getId(),student);

        assertEquals(name,student.getFirstName());
    }

    @Test
    public void update_notExistingStudent_NotFoundException() throws NotAllowedException, NotFoundException {
        thrown.expect(NotFoundException.class);

        studentService.update((long) 999,student);
    }

}
