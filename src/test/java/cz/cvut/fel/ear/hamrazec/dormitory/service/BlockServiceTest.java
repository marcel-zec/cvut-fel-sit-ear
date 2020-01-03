package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.BlockDao;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.AlreadyExistsException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Block;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Manager;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Role;
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

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class BlockServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private BlockService blockService;

    @Autowired
    private BlockDao blockDao;

    private Block block;

    private Manager manager;

    private Map<String, String> stringMap;
    private Map<String, Integer> stringIntMap;


    @Before
    public void before() {

        manager = new Manager();
        manager.setFirstName("Test");
        manager.setLastName("Test");
        manager.setUsername("Test");
        manager.setEmail("test@test.com");
        manager.setPassword("testpassword");
        manager.setWorkerNumber(50);
        em.persist(manager);

        block = new Block("Tst", "Test adress",6);
        em.persist(block);
    }


    @Test
    public void addManagerToBlock_WorksCorrect() throws NotFoundException, AlreadyExistsException {

        //test
        blockService.addManager("Tst", 50);
        assertEquals("Add manager not working", 1, block.getManagers().size());
    }


    @Test
    public void addManagerToBlockTwoTimes_NothingHappen() throws NotFoundException, AlreadyExistsException {
        blockService.addManager("Tst", 50);
        blockService.addManager("Tst", 50);
        assertEquals("Added same manager two time", 1, blockService.find("Tst").getManagers().size());
    }


    @Test
    public void addNotExistingManager_NotFoundException() throws NotFoundException, AlreadyExistsException {

        thrown.expect(NotFoundException.class);
        thrown.reportMissingExceptionWithMessage("Add not existing manager not working");

        //test
        blockService.addManager("Tst", 100);
    }


    @Test
    public void updateBlock_WorksCorrect() throws NotFoundException {
        //before test
        String newName = "BX";
        String newAddress = "NewTestAddress";

        //test
        blockService.update(block.getName(), newName, newAddress);
        assertEquals("Update not work for name", newName, block.getName());
        assertEquals("Update not work for address", newAddress, block.getAddress());
    }


    @Test
    public void updateNotExistingBlock_NotFoundException() throws NotFoundException {

        thrown.expect(NotFoundException.class);
        thrown.reportMissingExceptionWithMessage("Trying update not existing block");

        //test
        blockService.update("", "aaa","address");
    }

    @Test
    public void deleteEmptyBlock_WorksCorrect() throws Exception {
        //test
        blockService.delete(block.getName());
        assertNull("Simple delete block not working",blockService.find(block.getName()));
    }

    //TODO - zlozitejsie mazanie ak bude vymyslene kedy mozem mazat blok
}
