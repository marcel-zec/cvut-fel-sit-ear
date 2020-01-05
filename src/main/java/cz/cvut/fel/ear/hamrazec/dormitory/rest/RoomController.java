package cz.cvut.fel.ear.hamrazec.dormitory.rest;

import cz.cvut.fel.ear.hamrazec.dormitory.exception.*;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Accommodation;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Room;
import cz.cvut.fel.ear.hamrazec.dormitory.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/rooms")
@Validated
@PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_SUPERUSER')")
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

    @GetMapping(value = "/{number}/block/{name}",produces = MediaType.APPLICATION_JSON_VALUE)
    public Room getRoom(@PathVariable Integer number, @PathVariable String name) throws NotFoundException {

        return roomService.find(name, number);
    }

    @GetMapping(value = "/{number}/block/{name}/accommodations", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Accommodation> getActualAccommodationsInRoom(@PathVariable Integer number, @PathVariable String name) throws NotFoundException {

        return roomService.getActualAccommodations(name,number);
    }

    @GetMapping(value = "/free/block/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Room> getFreeRooms(@PathVariable String name, @RequestParam(name = "start") String dateStart, @RequestParam(name = "end") String dateEnd) throws NotFoundException
    {
        return roomService.findFreeRooms(name,LocalDate.parse(dateStart), LocalDate.parse(dateEnd));
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping(value = "/free/block/{name}",produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Integer> getFreeRoomsStudent(@PathVariable String name, @RequestParam(name = "start") String dateStart, @RequestParam(name = "end") String dateEnd) throws NotFoundException{

        return roomService.findFreeRoomsStudent(name,LocalDate.parse(dateStart), LocalDate.parse(dateEnd));
    }

    @PostMapping(value = "/block/{name}",consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createRoom(@RequestBody Room room, @PathVariable String name) throws NotFoundException, AlreadyExistsException, BadFloorException, NotAllowedException {

        roomService.addRoom(name,room);
        LOG.info("Room number {} created at block {}.", room.getRoomNumber(),room.getBlock().getName());
    }

    @DeleteMapping(value = "/{number}/block/{blockName}",consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoom(@PathVariable Integer number, @PathVariable String blockName, @RequestParam(defaultValue = "false") boolean accept) throws NotFoundException, AlreadyExistsException, BadFloorException, NotAcceptDeletingConsequences, NotAllowedException {
        if (accept){
            roomService.deleteRoom(number,blockName);
            LOG.info("Room number {} at block {} was deleted.", number, blockName);
        } else {
            LOG.info("Room number {} at block {} was deleted. User not accept possible consequences of deleting.", number, blockName);
            throw new NotAcceptDeletingConsequences();
        }
    }
}
