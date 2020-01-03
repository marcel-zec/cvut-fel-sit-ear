package cz.cvut.fel.ear.hamrazec.dormitory.rest;

import cz.cvut.fel.ear.hamrazec.dormitory.exception.AlreadyExistsException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.BadFloorException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotAcceptDeletingConsequences;
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

    @GetMapping(value = "/{number}/block/{name}",produces = MediaType.APPLICATION_JSON_VALUE)
    public Room getRoom(@PathVariable Integer number, @PathVariable String name) {

        return roomService.find(name, number);
    }

    @GetMapping(value = "/{number}/block/{name}/accommodations", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Accommodation> getActualAccommodationsInRoom(@PathVariable Integer number, @PathVariable String name) {

        return roomService.getActualAccommodations(name,number);
    }

    @GetMapping(value = "/free/block/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Room> getFreeRooms(@PathVariable String name, @RequestParam(name = "start") String dateStart, @RequestParam(name = "end") String dateEnd) throws NotFoundException
    {
        return roomService.findFreeRooms(name,LocalDate.parse(dateStart), LocalDate.parse(dateEnd));
    }


    @PostMapping(value = "/block/{name}",consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createRoom(@RequestBody Room room, @PathVariable String name) throws NotFoundException, AlreadyExistsException, BadFloorException {

        roomService.addRoom(name,room);
        LOG.info("Room number {} created at block {}.", room.getRoomNumber(),room.getBlock().getName());
    }

    @DeleteMapping(value = "/{number}/block/{blockName}",consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoom(@PathVariable Integer number, @PathVariable String blockName, @RequestParam(defaultValue = "false") boolean accept) throws NotFoundException, AlreadyExistsException, BadFloorException, NotAcceptDeletingConsequences {
        if (accept){
            roomService.deleteRoom(number,blockName);
            LOG.info("Room number {} at block {} was deleted.", number, blockName);
        } else {
            LOG.info("Room number {} at block {} was deleted. User not accept possible consequences of deleting.", number, blockName);
            throw new NotAcceptDeletingConsequences();
        }
    }
}
