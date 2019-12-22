package cz.cvut.fel.ear.hamrazec.dormitory.rest;

import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Accommodation;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Student;
import cz.cvut.fel.ear.hamrazec.dormitory.service.AccommodationService;
import cz.cvut.fel.ear.hamrazec.dormitory.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accommodations")
@Validated
public class AccommodationController {

    AccommodationService acomService;
    StudentService studentService;
    private static final Logger LOG = LoggerFactory.getLogger(AccommodationController.class);


    @Autowired
    public AccommodationController(AccommodationService acomService, StudentService studentService) {
        this.acomService = acomService;
        this.studentService = studentService;
    }

    @PostMapping(value = "/student/{idStudent}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createAccommodation(@RequestBody Accommodation accommodation) {

        try {
            acomService.create(accommodation);
            LOG.info("Accommodation with id {} created", accommodation.getId());
        }catch (NotFoundException e) {
            //TODO - exceptions
        }
    }

    @DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAccommodation(@PathVariable Long id) throws NotFoundException {
            acomService.delete(id);
            LOG.info("Accommodation with id {} removed.", id);
    }
}
