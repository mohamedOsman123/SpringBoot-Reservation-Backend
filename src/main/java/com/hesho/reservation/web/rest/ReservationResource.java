package com.hesho.reservation.web.rest;

import com.hesho.reservation.domain.Reservation;
import com.hesho.reservation.domain.User;
import com.hesho.reservation.domain.enumeration.ReservationStatus;
import com.hesho.reservation.security.AuthoritiesConstants;
import com.hesho.reservation.security.SecurityUtils;
import com.hesho.reservation.service.ReservationService;
import com.hesho.reservation.service.UserService;
import com.hesho.reservation.web.rest.errors.BadRequestAlertException;
import com.hesho.reservation.service.dto.ReservationDTO;
import com.hesho.reservation.service.dto.ReservationCriteria;
import com.hesho.reservation.service.ReservationQueryService;

import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.hesho.reservation.domain.Reservation}.
 */
@RestController
@RequestMapping("/api")
public class ReservationResource {

    private final Logger log = LoggerFactory.getLogger(ReservationResource.class);

    private static final String ENTITY_NAME = "reservation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReservationService reservationService;

    private final ReservationQueryService reservationQueryService;

    private final UserService userService;

    public ReservationResource(ReservationService reservationService, ReservationQueryService reservationQueryService,UserService userService) {
        this.reservationService = reservationService;
        this.reservationQueryService = reservationQueryService;
        this.userService=userService;
    }

    /**
     * {@code POST  /reservations} : Create a new reservation.
     *
     * @param reservationDTO the reservationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new reservationDTO, or with status {@code 400 (Bad Request)} if the reservation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/reservations")
    public ResponseEntity<ReservationDTO> createReservation(@RequestBody ReservationDTO reservationDTO) throws URISyntaxException {
        log.debug("REST request to save Reservation : {}", reservationDTO);
        Optional<User> user = userService.findCurrentUser();
        if (!user.isPresent()) {
            throw new BadRequestAlertException("No user detected", ENTITY_NAME, "noUser");
        }
        if (reservationDTO.getId() != null) {
            throw new BadRequestAlertException("A new reservation cannot already have an ID", ENTITY_NAME, "idExists");
        }
        reservationDTO.setUserId(user.get().getId());
        ReservationDTO result = reservationService.save(reservationDTO);
        return ResponseEntity.ok().body(result);
    }

    /**
     * {@code PUT  /reservations} : Updates an existing reservation.
     *
     * @param reservationDTO the reservationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reservationDTO,
     * or with status {@code 400 (Bad Request)} if the reservationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the reservationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/reservations")
    public ResponseEntity<ReservationDTO> updateReservation(@RequestBody ReservationDTO reservationDTO) throws URISyntaxException {
        log.debug("REST request to update Reservation : {}", reservationDTO);
        Optional<User> user = userService.findCurrentUser();
        if (!user.isPresent()) {
            throw new BadRequestAlertException("No user detected", ENTITY_NAME, "noUser");
        }
        if (reservationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idNull");
        }
        Optional<ReservationDTO> oldReservation = reservationService.findOne(reservationDTO.getId(),user.get());
        if (!oldReservation.isPresent()) {
            throw new BadRequestAlertException("Reservation Not Registered with User", ENTITY_NAME, "NoReservation");
        }
        reservationDTO.setUserId(user.get().getId());
        ReservationDTO result = reservationService.save(reservationDTO);
        return ResponseEntity.ok().body(result);
    }

    /**
     * {@code PUT  /reservations} : Updates an existing status reservation.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reservationDTO,
     * or with status {@code 400 (Bad Request)} if the reservationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the reservationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/reservations/updateStatus/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<ReservationDTO> updateReservationStatus(@PathVariable Long id,@RequestBody ReservationStatus status) throws URISyntaxException {
        log.debug("REST request to update Reservation Status: {}", id);

        ReservationDTO result = reservationService.updateStatus(id,status);
        return ResponseEntity.ok().body(result);
    }

    /**
     * {@code PUT  /reservations} : Cancel an existing status reservation.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reservationDTO,
     * or with status {@code 400 (Bad Request)} if the reservationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the reservationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/reservations/canceled/{id}")
    public ResponseEntity<ReservationDTO> cancelReservation(@PathVariable Long id) throws URISyntaxException {
        log.debug("REST request to cancel Reservation Status: {}", id);
        Optional<User> user = userService.findCurrentUser();
        if (!user.isPresent()) {
            throw new BadRequestAlertException("No user detected", ENTITY_NAME, "noUser");
        }
        Optional<ReservationDTO> reservationDTO = reservationService.findOne(id,user.get());
        if (!reservationDTO.isPresent()) {
            throw new BadRequestAlertException("No Reservation detected", ENTITY_NAME, "NoReservation");
        }
        ReservationDTO result = reservationService.cancelReservation(id);
        return ResponseEntity.ok().body(result);
    }

    /**
     * {@code GET  /reservations} : get all the reservations.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of reservations in body.
     */
    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationDTO>> getAllReservations(ReservationCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Reservations by criteria: {}", criteria);
        Page<ReservationDTO> page = reservationQueryService.findByCriteria(limitToUserData(criteria), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
    /**
     * {@code GET  /reservations/count} : count all the reservations.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/reservations/count")
    public ResponseEntity<Long> countReservations(ReservationCriteria criteria) {
        log.debug("REST request to count Reservations by criteria: {}", criteria);
        return ResponseEntity.ok().body(reservationQueryService.countByCriteria(limitToUserData(criteria)));
    }
    /**
     * {@code GET  /reservations/:id} : get the "id" reservation.
     *
     * @param id the id of the reservationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the reservationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/reservations/{id}")
    public ResponseEntity<ReservationDTO> getReservation(@PathVariable Long id) {
        log.debug("REST request to get Reservation : {}", id);
        Optional<User> user = userService.findCurrentUser();
        if (!user.isPresent()) {
            throw new BadRequestAlertException("No user detected", ENTITY_NAME, "noUser");
        }
        Optional<ReservationDTO> reservationDTO = reservationService.findOne(id,user.get());
        return ResponseUtil.wrapOrNotFound(reservationDTO);
    }
    /**
     * {@code DELETE  /reservations/:id} : delete the "id" reservation.
     *
     * @param id the id of the reservationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        log.debug("REST request to delete Reservation : {}", id);
        Optional<User> user = userService.findCurrentUser();
        if (!user.isPresent()) {
            throw new BadRequestAlertException("No user detected", ENTITY_NAME, "noUser");
        }
        Optional<ReservationDTO> reservationDTO = reservationService.findOne(id,user.get());
        if (!reservationDTO.isPresent()) {
            throw new BadRequestAlertException("No Reservation detected", ENTITY_NAME, "NoReservation");
        }
        reservationService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
    private ReservationCriteria limitToUserData(ReservationCriteria criteria) {
        if (!SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)) {
            Optional<User> user = userService.getUserWithAuthorities();
            if (!user.isPresent()) {
                throw new BadRequestAlertException("No User detected", ENTITY_NAME, "noUser");
            }
            Long userId = user.get().getId();
            LongFilter longFilter = new LongFilter();
            longFilter.setEquals(userId);
            criteria.setUserId(longFilter);
        }
        return criteria;
    }
}
