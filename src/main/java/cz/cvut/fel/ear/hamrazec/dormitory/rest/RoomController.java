package cz.cvut.fel.ear.hamrazec.dormitory.rest;

import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Accommodation;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Room;
import cz.cvut.fel.ear.hamrazec.dormitory.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/rooms")
@Validated
public class RoomController {

    private static final Logger LOG = LoggerFactory.getLogger(RoomController.class);
    private RoomService roomService;

    @Autowired
    public RoomController(RoomService service) {
        this.roomService = service;
    }

    @GetMapping(value = "/block/{name}",produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Room> getRooms(@PathVariable String name) throws NotFoundException {

        return roomService.findAll(name);
    }

    @GetMapping(value = "block/{name}/room/{number}",produces = MediaType.APPLICATION_JSON_VALUE)
    public Room getRoom(@PathVariable String name, @PathVariable Integer number) {

        return roomService.find(name, number);
    }

    @GetMapping(value = "block/{name}/room/{roomNum}/accommodations/", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Accommodation> getActualAccommodationsInRoom(@PathVariable Integer roomNum, @PathVariable String name) {

        return roomService.getActualAccommodations(name,roomNum);
    }

    @GetMapping(value = "/free_rooms/block/{name}/start/{dateStart}/end/{dateEnd}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Room> getFreeRooms(@PathVariable String name, @PathVariable String dateStart, @PathVariable String dateEnd) throws NotFoundException
    {
        return roomService.findFreeRooms(name,LocalDate.parse(dateStart), LocalDate.parse(dateEnd));
    }


    @PostMapping(value = "/block/{name}",consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createRoom(@RequestBody Room room, @PathVariable String name) throws NotFoundException {

        roomService.addRoom(name,room);
        LOG.info("Student with id {} created.", room.getId());
    }
}
