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
        manager.setPassword("testovacieheslo");
        manager.setRole(Role.MANAGER);
        manager.setWorkerNumber(50);
        em.persist(manager);

        block = new Block("Tst", "Testovacia adresa");
        em.persist(block);
    }


    @Test
    public void addManagerToBlock_WorksCorrect() throws NotFoundException {
        //before test
        stringIntMap = new HashMap<>();
        stringIntMap.put("manager", 50);

        //test
        blockService.addManager("Tst", stringIntMap);
        assertEquals("Add manager not working", 1 , block.getManagers().size() );
    }

    @Test
    public void addManagerToBlockTwoTimes_NothingHappen() throws NotFoundException {
        //before test
        stringIntMap = new HashMap<>();
        stringIntMap.put("manager", 50);

        //test
        blockService.addManager("Tst", stringIntMap);
        blockService.addManager("Tst", stringIntMap);
        assertEquals("Added same manager two time", 1 , blockService.find("Tst").getManagers().size() );
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
}
