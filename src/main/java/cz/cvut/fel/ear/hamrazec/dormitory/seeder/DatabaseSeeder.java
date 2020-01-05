package cz.cvut.fel.ear.hamrazec.dormitory.seeder;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.*;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.AlreadyExistsException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCrypt;
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
        student.setFirstName("zdeno");
        student.setUsername("username");
        student.setLastName("zly");
        student.setPassword(new BCryptPasswordEncoder().encode("heslo"));
        studentDao.persist(student);

        Student student1 = new Student();
        student1.setGender(Gender.MAN);
        student1.setBankAccountNumber("AB12345678912343");
        student1.setBirth(LocalDate.parse("2007-12-03"));
        student1.setEndOfStudy(LocalDate.parse("2022-12-03"));
        student1.setUniversity("CVUT");
        student1.setEmail("test2@test.com");
        student1.setFirstName("jozko");
        student1.setUsername("username1");
        student1.setLastName("mrkva");
        student1.setPassword("fefebebssvss");
        studentDao.persist(student1);

        Student student2 = new Student();
        student2.setGender(Gender.WOMAN);
        student2.setBankAccountNumber("AB12345678912343");
        student2.setBirth(LocalDate.parse("2007-12-03"));
        student2.setEndOfStudy(LocalDate.parse("2022-12-03"));
        student2.setUniversity("CVUT");
        student2.setEmail("test3@test.com");
        student2.setFirstName("Peter");
        student2.setUsername("username2");
        student2.setLastName("plachy");
        student2.setPassword("fefebebssvcss");
        //student.setPassword(new BCryptPasswordEncoder().encode("heslo"));
        studentDao.persist(student2);
    }

    void seedBlocks(){
        blockDao.persist(new Block("b1","Vanickova 7, Praha 6",6));
        blockDao.persist(new Block("b2","Vanickova 8, Praha 6",6));
        blockDao.persist(new Block("b3","Vanickova 9, Praha 6",6));
        blockDao.persist(new Block("b4","Vanickova 10, Praha 6",6));
        blockDao.persist(new Block("b5","Olympijska 6, Praha 6",6));
        blockDao.persist(new Block("b6","Olympijska 5, Praha 6",6));
        blockDao.persist(new Block("b7","Olympijska 4, Praha 6",6));
        blockDao.persist(new Block("b8","Olympijska 3, Praha 6",6));
    }

    void seedManagers() throws AlreadyExistsException {
        Manager manager = new Manager();
        manager.setFirstName("Jan");
        manager.setLastName("Novotny");
        manager.setEmail("novotny@email.com");
        manager.setPassword(new BCryptPasswordEncoder().encode("heslo"));
        manager.setWorkerNumber(1);
        manager.setUsername("janko");
        //manager.addBlock(blockDao.find("b1"));
        managerDao.persist(manager);

        manager = new Manager();
        manager.setFirstName("Peter");
        manager.setLastName("Novak");
        manager.setEmail("novak@email.com");
        manager.setPassword("fegwgeevasvv");
        //manager.setPassword(new BCryptPasswordEncoder().encode("heslo"));
        manager.setWorkerNumber(2);
        manager.setUsername("petko345");
        managerDao.persist(manager);

        manager = new Manager();
        manager.setFirstName("Karolina");
        manager.setLastName("Vesela");
        manager.setEmail("vesela@email.com");
        manager.setPassword("faefdafefegvav");
        //manager.setPassword(new BCryptPasswordEncoder().encode("heslo"));
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
        room2.setBlock(blockDao.find((long) 2));
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

        Accommodation accommodation2 = new Accommodation();
        accommodation2.setDateStart(LocalDate.now());
        accommodation2.setDateEnd(LocalDate.parse("2020-12-21"));
        accommodation2.setStatus(Status.ACC_ACTIVE);
        accommodation2.setRoom(roomDao.find("b1",334));
        accommodation2.getRoom().addActualAccomodation(accommodation2);
        accommodation2.setStudent(studentDao.find((long) 2));
        accommodationDao.persist(accommodation2);

//        Accommodation accommodation3 = new Accommodation();
//        accommodation3.setDateStart(LocalDate.now());
//        accommodation3.setDateEnd(LocalDate.parse("2020-12-21"));
//        accommodation3.setStatus(Status.ACC_ACTIVE);
//        accommodation3.setRoom(roomDao.find("b1",334));
//        accommodation3.getRoom().addActualAccomodation(accommodation3);
//        accommodation3.setStudent(studentDao.find((long) 3));
//        accommodationDao.persist(accommodation3);

            Accommodation accommodation1 = new Accommodation();
            accommodation1.setDateStart(LocalDate.parse("2019-10-10"));
            accommodation1.setDateEnd(LocalDate.parse("2019-12-21"));
            accommodation1.setStatus(Status.ACC_ENDED);
            accommodation1.setRoom(roomDao.find("b1",334));
            accommodation1.getRoom().addPastAccomodation(accommodation1);
            accommodation1.setStudent(studentDao.find((long) 1));
            accommodationDao.persist(accommodation1);
    }
    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        seedUsersTable();
        seedBlocks();
        try {
            seedManagers();
        } catch (AlreadyExistsException e) {
            e.printStackTrace();
        }
        seedRoom();
        seedAccom();
    }
}