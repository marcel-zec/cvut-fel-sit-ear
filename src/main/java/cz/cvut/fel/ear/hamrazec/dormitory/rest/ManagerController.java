package cz.cvut.fel.ear.hamrazec.dormitory.rest;

import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Manager;
import cz.cvut.fel.ear.hamrazec.dormitory.service.ManagerService;
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
@RequestMapping("/managers")
@Validated
public class ManagerController {

    private static final Logger LOG = LoggerFactory.getLogger(ManagerController.class);
    private ManagerService managerService;


    @Autowired
    public ManagerController(ManagerService managerService) {

        this.managerService = managerService;
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Manager> getManagers() {

        return managerService.findAll();
    }


    @GetMapping(value = "/{workerNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Manager getManager(@PathVariable Integer workerNumber) {

        return managerService.find(workerNumber);
    }


    @DeleteMapping(value = "/{workerNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteManager(@PathVariable Integer workerNumber) throws NotFoundException {

        managerService.delete(workerNumber);
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createManager(@RequestBody Manager manager) {

        managerService.create(manager);
        LOG.info("Category with id {} and workerNumber {} created", manager.getId(),manager.getWorkerNumber());
    }


    @PatchMapping(value = "/{workerNumber}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateManager(@PathVariable Integer workerNumber, @RequestBody Manager manager) throws NotFoundException {

        managerService.update(workerNumber, manager);
        LOG.info("Manager with workerNumber {} updated.", workerNumber);

    }

}
