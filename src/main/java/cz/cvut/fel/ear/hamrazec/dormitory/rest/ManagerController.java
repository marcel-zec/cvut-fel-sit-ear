package cz.cvut.fel.ear.hamrazec.dormitory.rest;

import cz.cvut.fel.ear.hamrazec.dormitory.model.Manager;
import cz.cvut.fel.ear.hamrazec.dormitory.service.ManagerService;
import javassist.NotFoundException;
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


    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Manager getManager(@PathVariable Long id) {

        return managerService.find(id);
    }


    @DeleteMapping(value = "/{managerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteManager(@PathVariable Long managerId) {

        managerService.delete(managerId);
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createManager(@RequestBody Manager manager) {

        managerService.create(manager);
        LOG.info("Category with id {} created", manager.getId());
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateManager(@PathVariable Long id, @RequestBody Map<String, String> map) throws NotFoundException {

        try {
            managerService.update(id, map);
            LOG.info("Manager with id {} updated.", id);
        } catch (javassist.NotFoundException e) {
            throw e;
        }
    }

}
