package cz.cvut.fel.ear.hamrazec.dormitory.rest;

import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Block;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Room;
import cz.cvut.fel.ear.hamrazec.dormitory.service.BlockService;
import cz.cvut.fel.ear.hamrazec.dormitory.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/blocks")
@Validated
public class BlockController {

    //TODO - pristup len pre superusera

    private static final Logger LOG = LoggerFactory.getLogger(BlockController.class);

    private BlockService blockService;
    private RoomService roomService;

    @Autowired
    public BlockController(BlockService blockService, RoomService roomService) {

        this.blockService = blockService;
        this.roomService = roomService;
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Block> getBlocks() {

        return blockService.findAll();
    }


//    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public Block getBlockById(@PathVariable Long id) throws NotFoundException {
//
//        Block block = blockService.find(id);
//        if (block == null) throw new NotFoundException();
//        return block;
//    }


    @GetMapping(value = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Block getBlockByName(@PathVariable String name) throws NotFoundException {

        Block block = blockService.find(name);
        if (block == null) throw new NotFoundException();
        return block;
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createBlock(@RequestBody Block block) {

        blockService.create(block);
        LOG.info("Block with id {} created.", block.getId());
    }


    @PostMapping(value = "/{blockName}/managers", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addManagerToBlock(@PathVariable String blockName, @RequestBody Map<String, Integer> request) {

        try {
            blockService.addManager(blockName, request);
            LOG.info("Manager with worker number {} added to block {}.", request.get("manager"), blockName);
        } catch (NotFoundException e) {

        }

    }


    @GetMapping(value = "/{blockName}/rooms", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Room> getRoomsFromBlock(@PathVariable String blockName) throws NotFoundException {
        //TODO - exception
        return roomService.findAll(blockName);
    }


    @PostMapping(value = "/{blockName}/rooms", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addRoomToBlock(@PathVariable String blockName, @RequestBody Room room) {

        try {
            roomService.addRoom(blockName, room);
            LOG.info("Room with id {} added to block {}.", room.getId(), blockName);
        } catch (NotFoundException e) {

        }

    }


    @DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBlock(@PathVariable Long id) {

        try {
            blockService.delete(id);
            LOG.info("Block with id {} removed.", id);
        } catch (NotFoundException e) {

        } catch (Exception e) {
            //TODO - exceptions
        }
    }


    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBlock(@PathVariable Long id, @RequestBody Map<String, String> request) {

        try {
            blockService.update(id, request);
            LOG.info("Block with id {} updated.", id);
        } catch (NotFoundException e) {
            //TODO - exceptions
        }
    }


}
