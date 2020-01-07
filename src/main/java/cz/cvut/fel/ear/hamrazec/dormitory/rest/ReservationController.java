package cz.cvut.fel.ear.hamrazec.dormitory.rest;

import cz.cvut.fel.ear.hamrazec.dormitory.exception.EndOfStudyExpirationException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotAcceptDeletingConsequences;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotAllowedException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Reservation;
import cz.cvut.fel.ear.hamrazec.dormitory.security.SecurityUtils;
import cz.cvut.fel.ear.hamrazec.dormitory.service.ReservationService;
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
@RequestMapping("/reservations")
@Validated
public class ReservationController {

    private ReservationService reservationService;
    private static final Logger LOG = LoggerFactory.getLogger(ReservationController.class);


    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPERUSER', 'ROLE_MANAGER')")
    @GetMapping(value = "/block/{blockName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Reservation> getReservations(@PathVariable String blockName) throws NotFoundException, NotAllowedException {

        return reservationService.findAll(blockName);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPERUSER', 'ROLE_MANAGER', 'ROLE_STUDENT')")
    @GetMapping(value = "/student/{student_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Reservation getReservation(@PathVariable Long student_id) throws NotAllowedException {

        return reservationService.findbyStudent(student_id);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPERUSER', 'ROLE_MANAGER')")
    @PatchMapping(value = "{id}/approve", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void approveReservation(@PathVariable Long id) throws NotAllowedException, NotFoundException {

        reservationService.approveReservation(id);
        LOG.info("Reservation with id {} approved", id);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPERUSER', 'ROLE_MANAGER')")
    @PatchMapping(value = "{id}/denied", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deniedReservation(@PathVariable Long id) throws NotFoundException {

        reservationService.cancelReservation(id);
        LOG.info("Reservation with id {} denied", id);
    }


    @PreAuthorize("hasAnyRole('ROLE_SUPERUSER', 'ROLE_MANAGER')")
    @PostMapping(value = "/student/{student_id}/block/{block_name}/room/{room_number}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createReservation(@RequestBody Reservation reservation, @PathVariable Long student_id, @PathVariable String block_name, @PathVariable Integer room_number) throws NotFoundException, NotAllowedException, EndOfStudyExpirationException {

        reservationService.createNewReservation(reservation, student_id ,block_name, room_number);
        LOG.info("Reservation with id {} created", reservation.getId());
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @PostMapping(value = "/block/{block_name}/room/{room_number}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createStudentReservation(@RequestBody Reservation reservation, @PathVariable String block_name, @PathVariable Integer room_number) throws NotFoundException, NotAllowedException, EndOfStudyExpirationException {

        reservationService.createNewReservation(reservation,block_name, room_number);
        LOG.info("Reservation with id {} created", reservation.getId());
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @PostMapping(value = "/block/{blockName}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createReservationStudentRandom(@RequestBody Reservation reservation,  @PathVariable String blockName) throws NotFoundException, NotAllowedException, EndOfStudyExpirationException {

        reservationService.createNewReservationRandom(reservation, blockName);
        LOG.info("Reservation on room {} created", reservation.getRoom().getRoomNumber());
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPERUSER', 'ROLE_MANAGER')")
    @PostMapping(value = "/student/{student_id}/block/{blockName}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createReservationRandom(@RequestBody Reservation reservation, @PathVariable Long student_id, @PathVariable String blockName) throws NotFoundException, NotAllowedException, EndOfStudyExpirationException {

        reservationService.createNewReservationRandom(reservation, student_id, blockName);
        LOG.info("Reservation on room {} created", reservation.getRoom().getRoomNumber());
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelReservation(@RequestParam(defaultValue = "false") boolean accept) throws NotFoundException, NotAllowedException, NotAcceptDeletingConsequences {
        if (accept){
            reservationService.deleteReservation();
            LOG.info("Reservation of student with username " + SecurityUtils.getCurrentUser().getUsername() + " deleted");
        } else {
            LOG.info("Reservation was not deleted. User not accept possible consequences of deleting.");
            throw new NotAcceptDeletingConsequences();
        }
    }

}
