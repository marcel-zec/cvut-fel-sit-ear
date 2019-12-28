package cz.cvut.fel.ear.hamrazec.dormitory.rest;

import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Room;
import cz.cvut.fel.ear.hamrazec.dormitory.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping(value = "/block/{name}",consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createRoom(@RequestBody Room room, @PathVariable String name) throws NotFoundException {

        roomService.addRoom(name,room);
        LOG.info("Student with id {} created.", room.getId());
    }
}
