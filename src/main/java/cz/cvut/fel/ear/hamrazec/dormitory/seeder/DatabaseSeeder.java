package cz.cvut.fel.ear.hamrazec.dormitory.seeder;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.*;
import cz.cvut.fel.ear.hamrazec.dormitory.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private BlockDao blockDao;
    private ManagerDao managerDao;
    private RoomDao roomDao;
    private AccommodationDao accommodationDao;


    @Autowired
    public DatabaseSeeder(UserDao userDao, StudentDao studentDao, BlockDao blockDao, ManagerDao managerDao, RoomDao roomDao, AccommodationDao accommodationDao) {

        this.userDao = userDao;
        this.studentDao = studentDao;
        this.blockDao = blockDao;
        this.managerDao = managerDao;
        this.roomDao= roomDao;
        this.accommodationDao = accommodationDao;

    }


    void seedUsersTable() {

        System.out.println(studentDao);

        Student student = new Student();
        student.setGender(Gender.MAN);
        student.setBankAccountNumber("AB12345678912345");
        student.setBirth(LocalDate.parse("2007-12-03"));
        student.setEndOfStudy(LocalDate.parse("2022-12-03"));
        student.setUniversity("CVUT");
        student.setEmail("test1@test.com");
        student.setFirstName("jozko");
        student.setUsername("username");
        student.setLastName("mrkva");
        student.setPassword("fefebebssvsdebes");
        //student.setPassword(new BCryptPasswordEncoder().encode("heslo"));
        student.setRole(Role.STUDENT);
        studentDao.persist(student);
    }

    void seedBlocks(){
        blockDao.persist(new Block("b1","Vanickova 7, Praha 6"));
        blockDao.persist(new Block("b2","Vanickova 8, Praha 6"));
        blockDao.persist(new Block("b3","Vanickova 9, Praha 6"));
        blockDao.persist(new Block("b4","Vanickova 10, Praha 6"));
        blockDao.persist(new Block("b5","Olympijska 6, Praha 6"));
        blockDao.persist(new Block("b6","Olympijska 5, Praha 6"));
        blockDao.persist(new Block("b7","Olympijska 4, Praha 6"));
        blockDao.persist(new Block("b8","Olympijska 3, Praha 6"));
    }

    void seedManagers(){
        Manager manager = new Manager();
        manager.setFirstName("Jan");
        manager.setLastName("Novotny");
        manager.setEmail("novotny@email.com");
        manager.setPassword("kolvesvsvseve");
        //manager.setPassword(new BCryptPasswordEncoder().encode("heslo"));
        manager.setRole(Role.MANAGER);
        manager.setWorkerNumber(1);
        manager.setUsername("janko");
        managerDao.persist(manager);

        manager = new Manager();
        manager.setFirstName("Peter");
        manager.setLastName("Novak");
        manager.setEmail("novak@email.com");
        manager.setPassword("fegwgeevasvv");
        //manager.setPassword(new BCryptPasswordEncoder().encode("heslo"));
        manager.setRole(Role.MANAGER);
        manager.setWorkerNumber(2);
        manager.setUsername("petko345");
        managerDao.persist(manager);

        manager = new Manager();
        manager.setFirstName("Karolina");
        manager.setLastName("Vesela");
        manager.setEmail("vesela@email.com");
        manager.setPassword("faefdafefegvav");
        //manager.setPassword(new BCryptPasswordEncoder().encode("heslo"));
        manager.setRole(Role.MANAGER);
        manager.setWorkerNumber(3);
        manager.setUsername("kaja234");
        managerDao.persist(manager);
    }

    void seedRoom(){
        Room room = new Room();
        room.setBlock(blockDao.find((long) 1));
        room.setMaxCapacity(3);
        room.setFloor(3);
        room.setRoomNumber(334);
        roomDao.persist(room);

        Room room2 = new Room();
        room2.setBlock(blockDao.find((long) 1));
        room2.setMaxCapacity(3);
        room2.setFloor(4);
        room2.setRoomNumber(452);
        roomDao.persist(room2);
    }

    void seedAccom(){
        Accommodation accommodation = new Accommodation();
        accommodation.setDateStart(LocalDate.now());
        accommodation.setDateEnd(LocalDate.parse("2020-12-21"));
        accommodation.setStatus(Status.ACC_ACTIVE);
        accommodation.setRoom(roomDao.find("b1",334));
        accommodation.getRoom().addActualAccomodation(accommodation);
        accommodation.setStudent(studentDao.find((long) 1));
        accommodationDao.persist(accommodation);
    }
    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        seedUsersTable();
        seedBlocks();
        seedManagers();
        seedRoom();
        seedAccom();
    }
}