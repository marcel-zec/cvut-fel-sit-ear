package cz.cvut.fel.ear.hamrazec.dormitory.rest;

import cz.cvut.fel.ear.hamrazec.dormitory.exception.*;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Block;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Manager;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Room;
import cz.cvut.fel.ear.hamrazec.dormitory.service.BlockService;
import cz.cvut.fel.ear.hamrazec.dormitory.service.ManagerService;
import cz.cvut.fel.ear.hamrazec.dormitory.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/blocks")
@Validated
@PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_SUPERUSER')")
public class BlockController {

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


    @GetMapping(value = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Block getBlockByName(@PathVariable String name) throws NotFoundException {

        Block block = blockService.find(name);
        if (block == null) throw new NotFoundException();
        return block;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_SUPERUSER')")
    public void createBlock(@RequestBody Block block) {

        blockService.create(block);
        LOG.info("Block with id {} created.", block.getId());
    }


    @GetMapping(value = "/{blockName}/floor/{number}/rooms", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Room> getRoomsAtFloor(@PathVariable String blockName, @PathVariable Integer number) throws NotFoundException {

        return blockService.getRoomsAtFloor(blockName,number);
    }

    @GetMapping(value = "/{blockName}/rooms", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Room> getRoomsFromBlock(@PathVariable String blockName) throws NotFoundException {

        return roomService.findAll(blockName);
    }


    @PostMapping(value = "/{blockName}/managers/{workerNumber}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_SUPERUSER')")
    public void addManagerToBlock(@PathVariable String blockName, @PathVariable Integer workerNumber) throws NotFoundException, AlreadyExistsException {

        blockService.addManager(blockName, workerNumber);
        LOG.info("Manager with worker number {} added to block {}.", workerNumber, blockName);
    }


    @PatchMapping(value = "/{blockName}/managers/{workerNumber}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_SUPERUSER')")
    public void removeManagerFromBlock(@PathVariable String blockName, @PathVariable Integer workerNumber) throws NotFoundException {

        blockService.removeManager(blockName, workerNumber);
        LOG.info("Manager with workNumber {} removed from block {}", workerNumber, blockName);
    }

    @PatchMapping(value = "/{blockName}/floor", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_SUPERUSER')")
    public void changeAmountOfFloors(@PathVariable String blockName, @RequestParam Integer amount, @RequestParam(defaultValue = "false") boolean accept) throws NotFoundException, BadFloorException, NotAcceptDeletingConsequences, NotAllowedException {
        if (accept){
            blockService.changeAmountOfFloors(blockName, amount);
            LOG.info("Amount of floors at block {} was changed to {}.", blockName, amount);
        } else {
            LOG.info("Amount of floors at block {} not changed. User not accept possible consequences of deleting.", blockName);
            throw new NotAcceptDeletingConsequences();
        }
    }



    @PatchMapping(value = "/{blockName}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_SUPERUSER')")
    public void updateBlock(@PathVariable String blockName, @RequestBody Map<String, String> request) throws NotFoundException {

            blockService.update(blockName, request.get("name"), request.get("address"));
            LOG.info("Block with name {} updated.", blockName);
    }

    @DeleteMapping(value = "/{blockName}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_SUPERUSER')")
    public void removeBlock(@PathVariable String blockName, @RequestParam(defaultValue = "false") boolean accept) throws NotAcceptDeletingConsequences, NotFoundException, NotAllowedException {
        if (accept){
            blockService.delete(blockName);
            LOG.info("Block with name {} removed.", blockName);
        } else {
            LOG.info("Block {} not deleted. User not accept possible consequences of deleting.", blockName);
            throw new NotAcceptDeletingConsequences();
        }
    }

}
