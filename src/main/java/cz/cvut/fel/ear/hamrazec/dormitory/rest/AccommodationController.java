package cz.cvut.fel.ear.hamrazec.dormitory.rest;

import cz.cvut.fel.ear.hamrazec.dormitory.exception.AlreadyExistsException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotAllowedException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Accommodation;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Reservation;
import cz.cvut.fel.ear.hamrazec.dormitory.service.AccommodationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/accommodations")
@Validated
public class AccommodationController {

    private AccommodationService accommodationService;
    private static final Logger LOG = LoggerFactory.getLogger(AccommodationController.class);


    @Autowired
    public AccommodationController(AccommodationService accommodationService) {

        this.accommodationService = accommodationService;
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPERUSER', 'ROLE_MANAGER')")
    @GetMapping(value = "/block/{blockName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Accommodation> getAccommodations(@PathVariable String blockName) throws NotAllowedException, NotFoundException {

        return accommodationService.findAll(blockName);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPERUSER', 'ROLE_MANAGER', 'ROLE_STUDENT')")
    @GetMapping(value = "/student/{student_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Accommodation> getAccommodationsOfStudent(@PathVariable Long student_id) throws NotAllowedException {

        return accommodationService.findAll(student_id);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPERUSER', 'ROLE_MANAGER', 'ROLE_STUDENT')")
    @GetMapping(value = "/actual/student/{student_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Accommodation getActualAccommodationOfStudent(@PathVariable Long student_id) throws NotAllowedException {
        return accommodationService.findActualAccommodationOfStudent(student_id);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPERUSER', 'ROLE_MANAGER')")
    @PostMapping(value = "/student/{student_id}/block/{block_name}/room/{room_number}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createAccommodation(@RequestBody Accommodation accommodation, @PathVariable Long student_id, @PathVariable String block_name,
                                    @PathVariable int room_number) throws AlreadyExistsException, NotFoundException, NotAllowedException {

            accommodationService.create(accommodation, student_id, room_number, block_name);
            LOG.info("Accommodation with id {} created", accommodation.getId());
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPERUSER', 'ROLE_MANAGER')")
    @PostMapping(value = "/student/{student_id}/block/{blockName}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createAccommodationRandom(@RequestBody Accommodation accommodation, @PathVariable Long student_id, @PathVariable String blockName) throws NotFoundException, NotAllowedException {
        
            accommodationService.createNewAccommodationRandom(accommodation,student_id,blockName);
            LOG.info("Accommodation on room {} created", accommodation.getRoom().getRoomNumber());
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPERUSER', 'ROLE_MANAGER')")
    @PostMapping(value = "/reservation", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createAccommodationFromReservation(@RequestBody Reservation reservation) throws NotFoundException, NotAllowedException {

        accommodationService.createFromReservation(reservation);
        LOG.info("Accommodation on room {} created", reservation.getRoom().getRoomNumber());
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPERUSER', 'ROLE_MANAGER')")
    @DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAccommodation(@PathVariable Long id) throws NotFoundException {

            accommodationService.delete(id);
            LOG.info("Accommodation with id {} removed.", id);
    }
}
