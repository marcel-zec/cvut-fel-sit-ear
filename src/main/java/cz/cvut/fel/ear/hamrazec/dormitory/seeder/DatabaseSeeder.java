package cz.cvut.fel.ear.hamrazec.dormitory.seeder;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.UserDao;
import cz.cvut.fel.ear.hamrazec.dormitory.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Component
public class DatabaseSeeder {

    private Logger LOGGER = Logger.getLogger(DatabaseSeeder.class.getName());
    private UserDao userDao;

    @Autowired
    public DatabaseSeeder(UserDao userDao) {
        this.userDao = userDao;
    }

    public void seed() {
        seedUsersTable();
    }

    private void seedUsersTable() {
        List<User> users = userDao.findAll();
        User user = new User();
        user.setFirstName("Marcel");
        user.setLastName("Testovany");
        user.setUsername("tester");
        user.setEmail("test@test.com");
        user.setPassword(new BCryptPasswordEncoder().encode("heslo"));
        if (!users.contains(user)) {
            userDao.persist(user);
            LOGGER.info("User has been seeded");
        } else {
            userDao.update(user);
            LOGGER.warning("User already exist");
        }
    }
}