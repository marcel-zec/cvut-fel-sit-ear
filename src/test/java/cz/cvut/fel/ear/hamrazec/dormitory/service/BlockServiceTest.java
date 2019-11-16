package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.BlockDao;
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
        manager.setRole(Role.MANAGER);
        manager.setWorkerNumber(50);
        em.persist(manager);

        block = new Block("Tst", "Test adress");
        em.persist(block);
    }


    @Test
    public void addManagerToBlock_WorksCorrect() throws NotFoundException {
        //before test
        stringIntMap = new HashMap<>();
        stringIntMap.put("manager", 50);

        //test
        blockService.addManager("Tst", stringIntMap);
        assertEquals("Add manager not working", 1, block.getManagers().size());
    }


    @Test
    public void addManagerToBlockTwoTimes_NothingHappen() throws NotFoundException {
        //before test
        stringIntMap = new HashMap<>();
        stringIntMap.put("manager", 50);

        //test
        blockService.addManager("Tst", stringIntMap);
        blockService.addManager("Tst", stringIntMap);
        assertEquals("Added same manager two time", 1, blockService.find("Tst").getManagers().size());
    }


    @Test
    public void addNotExistingManager_NotFoundException() throws NotFoundException {

        thrown.expect(NotFoundException.class);
        thrown.reportMissingExceptionWithMessage("Add not existing manager not working");
        //before test
        stringIntMap = new HashMap<>();
        stringIntMap.put("manager", 100);

        //test
        blockService.addManager("Tst", stringIntMap);
    }


    @Test
    public void updateBlock_WorksCorrect() throws NotFoundException {
        //before test
        String newName = "BX";
        String newAddress = "NewTestAddress";
        stringMap = new HashMap<>();
        stringMap.put("name", newName);
        stringMap.put("address", newAddress);

        //test
        blockService.update(block.getName(), stringMap);
        assertEquals("Update not work for name", newName, block.getName());
        assertEquals("Update not work for address", newAddress, block.getAddress());
    }


    @Test
    public void updateNotExistingBlock_NotFoundException() throws NotFoundException {

        thrown.expect(NotFoundException.class);
        thrown.reportMissingExceptionWithMessage("Trying update not existing block");
        //before test
        stringMap = new HashMap<>();
        stringMap.put("name", "AAA");
        stringMap.put("address", "AAA");

        //test
        blockService.update("", stringMap);
    }

    @Test
    public void deleteEmptyBlock_WorksCorrect() throws Exception {
        //test
        blockService.delete(block.getName());
        assertNull("Simple delete block not working",blockService.find(block.getName()));
    }

    //TODO - zlozitejsie mazanie ak bude vymyslene kedy mozem mazat blok
}
