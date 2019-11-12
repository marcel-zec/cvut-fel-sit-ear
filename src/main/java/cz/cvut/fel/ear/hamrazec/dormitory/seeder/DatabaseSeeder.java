package cz.cvut.fel.ear.hamrazec.dormitory.seeder;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.StudentDao;
import cz.cvut.fel.ear.hamrazec.dormitory.dao.UserDao;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Gender;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Student;
import cz.cvut.fel.ear.hamrazec.dormitory.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

@Component
public class DatabaseSeeder implements
        ApplicationListener<ContextRefreshedEvent> {

    private Logger LOGGER = Logger.getLogger(DatabaseSeeder.class.getName());
    private UserDao userDao;
    private StudentDao studentDao;

    @Autowired
    public DatabaseSeeder(UserDao userDao,StudentDao studentDao) {
        this.userDao = userDao;
        this.studentDao = studentDao;
    }

    void seedUsersTable() {

        System.out.println(userDao);

        List<User> users = userDao.findAll();
        User user = new User();
        user.setFirstName("Jozko");
        user.setLastName("Mrkvicka");
        user.setUsername("mrkva");
        user.setEmail("test1@test.com");
        user.setPassword(new BCryptPasswordEncoder().encode("heslo"));

        Student student = new Student(Gender.MAN);
        student.setBankAccountNumber("AB12345678912345");
        student.setBirth(LocalDate.parse("2007-12-03"));
        student.setEndOfStudy(LocalDate.parse("2022-12-03"));
        student.setUniversity("CVUT");
        student.setUser(user);

        userDao.persist(user);
        studentDao.persist(student);

        LOGGER.info("User has been seeded");
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        seedUsersTable();
    }
}