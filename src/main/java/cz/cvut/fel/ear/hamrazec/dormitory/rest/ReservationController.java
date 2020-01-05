package cz.cvut.fel.ear.hamrazec.dormitory.rest;

import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotAllowedException;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Reservation;
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
    @GetMapping(value = "block/{blockName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Reservation> getReservations(@PathVariable String blockName) throws NotFoundException, NotAllowedException {

        return reservationService.findAll(blockName);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPERUSER', 'ROLE_MANAGER', 'ROLE_STUDENT')")
    @GetMapping(value = "student/{student_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Reservation getReservation(@PathVariable Long student_id) throws NotAllowedException {

        return reservationService.findbyStudent(student_id);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPERUSER', 'ROLE_MANAGER', 'ROLE_STUDENT')")
    @PostMapping(value = "student/{student_id}/block/{block_name}/room/{room_number}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createReservation(@RequestBody Reservation reservation, @PathVariable Long student_id, @PathVariable String block_name, @PathVariable Integer room_number) throws NotFoundException, NotAllowedException {

        reservationService.createNewReservation(reservation, student_id,block_name, room_number);
        LOG.info("Reservation with id {} created", reservation.getId());
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPERUSER', 'ROLE_MANAGER', 'ROLE_STUDENT')")
    @PostMapping(value = "student/{student_id}/block/{blockName}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createReservationRandom(@RequestBody Reservation reservation, @PathVariable Long student_id, @PathVariable String blockName) throws NotFoundException, NotAllowedException {

        reservationService.createNewReservationRandom(reservation, student_id, blockName);
        LOG.info("Reservation on room {} created", reservation.getRoom().getRoomNumber());
    }

}
