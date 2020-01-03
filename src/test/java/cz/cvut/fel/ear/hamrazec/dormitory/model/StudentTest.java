package cz.cvut.fel.ear.hamrazec.dormitory.model;

import cz.cvut.fel.ear.hamrazec.dormitory.environment.Generator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class StudentTest {

    static Student student;
    static Block block;
    Accommodation accommodation;

    @BeforeClass
    public static void init(){
        block = Generator.generateBlockWithRooms();
    }

    @Before
    public void beforeInit(){
        student = Generator.generateStudent();
    }

    @Test
    public void hasActiveAccommodation_has_workCorrect(){
        accommodation = Generator.generateActiveAccommodation(block.getRooms().get(0),student);
        List<Accommodation> list = new ArrayList<>();
        list.add(accommodation);
        student.setAccommodations(list);
        assertTrue(student.hasActiveAccommodation());
    }

    @Test
    public void hasActiveAccommodation_accommodationRemoved_workCorrect(){
        assertFalse(student.hasActiveAccommodation());
    }
}
