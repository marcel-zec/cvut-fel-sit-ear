package cz.cvut.fel.ear.hamrazec.dormitory.rest;

import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotAllowedException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Accommodation;
import cz.cvut.fel.ear.hamrazec.dormitory.service.AccommodationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/accommodations")
@Validated
public class AccommodationController {

    private AccommodationService acomService;
    private static final Logger LOG = LoggerFactory.getLogger(AccommodationController.class);


    @Autowired
    public AccommodationController(AccommodationService acomService) {
        this.acomService = acomService;
    }

    @GetMapping(value = "block/{blockName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Accommodation> getAccommodations(@PathVariable String blockName) {

        return acomService.findAll(blockName);
    }


    @PostMapping(value = "student/{student_id}/room/{room_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createAccommodation(@RequestBody Accommodation accommodation, @PathVariable Long student_id, @PathVariable Long room_id) {

        try {
            acomService.create(accommodation, student_id, room_id);
            LOG.info("Accommodation with id {} created", accommodation.getId());
        }catch (NotFoundException | NotAllowedException e) {
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
