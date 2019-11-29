package cz.cvut.fel.ear.hamrazec.dormitory.environment;

import cz.cvut.fel.ear.hamrazec.dormitory.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

public class Generator {

    private static final Random RAND = new Random();

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
        user.setBirth(LocalDate.of(1990,12,12));
        user.setBankAccountNumber("AAAAAAAAAA2222222222");
        user.setEmail("username" + randomInt() + "@test.cz");
        user.setEndOfStudy(LocalDate.of(2040,12,12));
        return user;
    }

    public static Block generateBlockWithRooms(){
        final Block block = new Block();
        block.setName("BT1");
        block.setAddress("Test address to block");
        ArrayList<Room> rooms = new ArrayList();

        Room room = new Room();
        room.setBlock(block);
        room.setFloor(1);
        room.setMaxCapacity(4);
        room.setRoomNumber(1);
        rooms.add(room);

        room = new Room();
        room.setBlock(block);
        room.setFloor(2);
        room.setMaxCapacity(4);
        room.setRoomNumber(2);
        rooms.add(room);

        room = new Room();
        room.setBlock(block);
        room.setFloor(3);
        room.setMaxCapacity(4);
        room.setRoomNumber(3);
        rooms.add(room);

        block.setRooms(rooms);
        return block;
    }

    public static Accommodation generateActiveAccommodation(Room room){
        Accommodation acco = new Accommodation();
        acco.setStatus(Status.ACC_ACTIVE);
        acco.setDateStart(LocalDate.parse("2010-10-10"));
        acco.setDateEnd(LocalDate.parse("2050-10-10"));
        acco.setRoom(room);
        return acco;
    }
}