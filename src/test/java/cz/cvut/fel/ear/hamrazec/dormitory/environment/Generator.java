package cz.cvut.fel.ear.hamrazec.dormitory.environment;

import cz.cvut.fel.ear.hamrazec.dormitory.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

public class Generator {

    private static final Random RAND = new Random();

    private static LocalDate startDate = LocalDate.parse("2010-10-10");
    private static LocalDate endDate = LocalDate.parse("2040-10-10");


    public static int randomInt() {

        return RAND.nextInt();
    }


    public static boolean randomBoolean() {

        return RAND.nextBoolean();
    }

    public static Student generateStudent() {

        final Student user = new Student();
        user.setFirstName("FirstName" + randomInt());
        user.setLastName("LastName" + randomInt());
        user.setUsername("username" + randomInt());
        user.setPassword(Integer.toString(randomInt()));
        user.setUniversity("CVUT");
        user.setBirth(LocalDate.of(1990, 12, 12));
        user.setBankAccountNumber("AAAAAAAAAA2222222222");
        user.setEmail("username" + randomInt() + "@test.cz");
        user.setEndOfStudy(LocalDate.of(2040, 12, 12));
        return user;
    }


    public static Block generateBlockWithRooms() {

        Block block = new Block();
        block.setName("BT1");
        block.setFloors(6);
        block.setAddress("Test address to block");
        ArrayList rooms = new ArrayList();

        Room room = new Room();
        room.setBlock(block);
        room.setFloor(1);
        room.setMaxCapacity(1);
        room.setRoomNumber(1);
        rooms.add(room);

        room = new Room();
        room.setBlock(block);
        room.setFloor(2);
        room.setMaxCapacity(2);
        room.setRoomNumber(2);
        rooms.add(room);

        room = new Room();
        room.setBlock(block);
        room.setFloor(3);
        room.setMaxCapacity(3);
        room.setRoomNumber(3);
        rooms.add(room);

        room = new Room();
        room.setBlock(block);
        room.setFloor(4);
        room.setMaxCapacity(4);
        room.setRoomNumber(4);
        rooms.add(room);

        block.setRooms(rooms);
        return block;
    }


    public static Accommodation generateActiveAccommodation(Room room, Student student, LocalDate start, LocalDate end) {

        Accommodation acco = new Accommodation();
        acco.setStatus(Status.ACC_ACTIVE);
        acco.setDateStart(start);
        acco.setDateEnd(end);
        acco.setRoom(room);
        acco.setStudent(student);
        room.addActualAccomodation(acco);
        return acco;
    }


    public static Accommodation generateActiveAccommodation(Room room, Student student) {

        return generateActiveAccommodation(room, student, startDate, endDate);
    }


    public static Reservation generateReservation(Room room,Student student, boolean approved, LocalDate start, LocalDate end) {

        Reservation res = new Reservation();
        if (approved) res.setStatus(Status.RES_APPROVED);
        else res.setStatus(Status.RES_PENDING);
        res.setDateStart(start);
        res.setDateEnd(end);
        res.setRoom(room);
        res.setStudent(student);
        room.addReservation(res);
        return res;
    }


    public static Reservation generateReservation(Room room, Student student, boolean approved) {

        return generateReservation(room, student, approved, startDate, endDate);
    }


}