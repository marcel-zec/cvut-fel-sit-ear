package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.environment.Generator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class ManagerServiceTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ManagerService managerService;


    @Test
    public void create_newManager_worksCorrect(){
        int sizeBefore = managerService.findAll().size();

        managerService.create(Generator.generateManager());
        assertEquals(sizeBefore+1,managerService.findAll().size());
    }
}
