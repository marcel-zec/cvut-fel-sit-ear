package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.environment.Generator;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Manager;
import cz.cvut.fel.ear.hamrazec.dormitory.model.SuperUser;
import cz.cvut.fel.ear.hamrazec.dormitory.security.SecurityUtils;
import cz.cvut.fel.ear.hamrazec.dormitory.security.model.UserDetails;
import org.junit.Before;
import org.junit.Test;
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

    @Before
    public void before(){
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
    }

    @Test
    public void create_newManager_worksCorrect(){
        int sizeBefore = managerService.findAll().size();

        Manager manager = new Manager();
        manager.setPassword("dwfiv492925efov");
        manager.setUsername("manageris");
        manager.setEmail("milan@jo.cz");
        manager.setLastName("Dlhan");
        manager.setFirstName("Peter");
        manager.setWorkerNumber(60);
        managerService.create(manager);
        assertEquals(sizeBefore+1,managerService.findAll().size());
    }
}
