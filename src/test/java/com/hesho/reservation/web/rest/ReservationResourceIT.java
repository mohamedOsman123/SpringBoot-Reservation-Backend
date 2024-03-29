package com.hesho.reservation.web.rest;

import com.hesho.reservation.ReservationBackendApp;
import com.hesho.reservation.domain.Reservation;
import com.hesho.reservation.domain.User;
import com.hesho.reservation.domain.Place;
import com.hesho.reservation.repository.ReservationRepository;
import com.hesho.reservation.service.ReservationService;
import com.hesho.reservation.service.dto.ReservationDTO;
import com.hesho.reservation.service.mapper.ReservationMapper;
import com.hesho.reservation.service.dto.ReservationCriteria;
import com.hesho.reservation.service.ReservationQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.hesho.reservation.domain.enumeration.ReservationType;
import com.hesho.reservation.domain.enumeration.ReservationStatus;
/**
 * Integration tests for the {@link ReservationResource} REST controller.
 */
@SpringBootTest(classes = ReservationBackendApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class ReservationResourceIT {

    private static final ReservationType DEFAULT_TYPE = ReservationType.DAILY;
    private static final ReservationType UPDATED_TYPE = ReservationType.WEEKLY;

    private static final ReservationStatus DEFAULT_STATUS = ReservationStatus.PENDING;
    private static final ReservationStatus UPDATED_STATUS = ReservationStatus.APPROVED;

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Double DEFAULT_FEES = 1D;
    private static final Double UPDATED_FEES = 2D;
    private static final Double SMALLER_FEES = 1D - 1D;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationQueryService reservationQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReservationMockMvc;

    private Reservation reservation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reservation createEntity(EntityManager em) {
        Reservation reservation = new Reservation()
            .type(DEFAULT_TYPE)
            .status(DEFAULT_STATUS)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .fees(DEFAULT_FEES);
        return reservation;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reservation createUpdatedEntity(EntityManager em) {
        Reservation reservation = new Reservation()
            .type(UPDATED_TYPE)
            .status(UPDATED_STATUS)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .fees(UPDATED_FEES);
        return reservation;
    }

    @BeforeEach
    public void initTest() {
        reservation = createEntity(em);
    }

    @Test
    @Transactional
    public void createReservation() throws Exception {
        int databaseSizeBeforeCreate = reservationRepository.findAll().size();
        // Create the Reservation
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);
        restReservationMockMvc.perform(post("/api/reservations")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(reservationDTO)))
            .andExpect(status().isCreated());

        // Validate the Reservation in the database
        List<Reservation> reservationList = reservationRepository.findAll();
        assertThat(reservationList).hasSize(databaseSizeBeforeCreate + 1);
        Reservation testReservation = reservationList.get(reservationList.size() - 1);
        assertThat(testReservation.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testReservation.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testReservation.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testReservation.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testReservation.getFees()).isEqualTo(DEFAULT_FEES);
    }

    @Test
    @Transactional
    public void createReservationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = reservationRepository.findAll().size();

        // Create the Reservation with an existing ID
        reservation.setId(1L);
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        // An entity with an existing ID cannot be created, so this API call must fail
        restReservationMockMvc.perform(post("/api/reservations")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(reservationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Reservation in the database
        List<Reservation> reservationList = reservationRepository.findAll();
        assertThat(reservationList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllReservations() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList
        restReservationMockMvc.perform(get("/api/reservations?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reservation.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].fees").value(hasItem(DEFAULT_FEES.doubleValue())));
    }
    
    @Test
    @Transactional
    public void getReservation() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        // Get the reservation
        restReservationMockMvc.perform(get("/api/reservations/{id}", reservation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(reservation.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.fees").value(DEFAULT_FEES.doubleValue()));
    }


    @Test
    @Transactional
    public void getReservationsByIdFiltering() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        Long id = reservation.getId();

        defaultReservationShouldBeFound("id.equals=" + id);
        defaultReservationShouldNotBeFound("id.notEquals=" + id);

        defaultReservationShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultReservationShouldNotBeFound("id.greaterThan=" + id);

        defaultReservationShouldBeFound("id.lessThanOrEqual=" + id);
        defaultReservationShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllReservationsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where type equals to DEFAULT_TYPE
        defaultReservationShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the reservationList where type equals to UPDATED_TYPE
        defaultReservationShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllReservationsByTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where type not equals to DEFAULT_TYPE
        defaultReservationShouldNotBeFound("type.notEquals=" + DEFAULT_TYPE);

        // Get all the reservationList where type not equals to UPDATED_TYPE
        defaultReservationShouldBeFound("type.notEquals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllReservationsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultReservationShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the reservationList where type equals to UPDATED_TYPE
        defaultReservationShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllReservationsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where type is not null
        defaultReservationShouldBeFound("type.specified=true");

        // Get all the reservationList where type is null
        defaultReservationShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    public void getAllReservationsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where status equals to DEFAULT_STATUS
        defaultReservationShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the reservationList where status equals to UPDATED_STATUS
        defaultReservationShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllReservationsByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where status not equals to DEFAULT_STATUS
        defaultReservationShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the reservationList where status not equals to UPDATED_STATUS
        defaultReservationShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllReservationsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultReservationShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the reservationList where status equals to UPDATED_STATUS
        defaultReservationShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllReservationsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where status is not null
        defaultReservationShouldBeFound("status.specified=true");

        // Get all the reservationList where status is null
        defaultReservationShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    public void getAllReservationsByStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where startDate equals to DEFAULT_START_DATE
        defaultReservationShouldBeFound("startDate.equals=" + DEFAULT_START_DATE);

        // Get all the reservationList where startDate equals to UPDATED_START_DATE
        defaultReservationShouldNotBeFound("startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllReservationsByStartDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where startDate not equals to DEFAULT_START_DATE
        defaultReservationShouldNotBeFound("startDate.notEquals=" + DEFAULT_START_DATE);

        // Get all the reservationList where startDate not equals to UPDATED_START_DATE
        defaultReservationShouldBeFound("startDate.notEquals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllReservationsByStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where startDate in DEFAULT_START_DATE or UPDATED_START_DATE
        defaultReservationShouldBeFound("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE);

        // Get all the reservationList where startDate equals to UPDATED_START_DATE
        defaultReservationShouldNotBeFound("startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllReservationsByStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where startDate is not null
        defaultReservationShouldBeFound("startDate.specified=true");

        // Get all the reservationList where startDate is null
        defaultReservationShouldNotBeFound("startDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllReservationsByEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where endDate equals to DEFAULT_END_DATE
        defaultReservationShouldBeFound("endDate.equals=" + DEFAULT_END_DATE);

        // Get all the reservationList where endDate equals to UPDATED_END_DATE
        defaultReservationShouldNotBeFound("endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void getAllReservationsByEndDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where endDate not equals to DEFAULT_END_DATE
        defaultReservationShouldNotBeFound("endDate.notEquals=" + DEFAULT_END_DATE);

        // Get all the reservationList where endDate not equals to UPDATED_END_DATE
        defaultReservationShouldBeFound("endDate.notEquals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void getAllReservationsByEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where endDate in DEFAULT_END_DATE or UPDATED_END_DATE
        defaultReservationShouldBeFound("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE);

        // Get all the reservationList where endDate equals to UPDATED_END_DATE
        defaultReservationShouldNotBeFound("endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void getAllReservationsByEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where endDate is not null
        defaultReservationShouldBeFound("endDate.specified=true");

        // Get all the reservationList where endDate is null
        defaultReservationShouldNotBeFound("endDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllReservationsByFeesIsEqualToSomething() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where fees equals to DEFAULT_FEES
        defaultReservationShouldBeFound("fees.equals=" + DEFAULT_FEES);

        // Get all the reservationList where fees equals to UPDATED_FEES
        defaultReservationShouldNotBeFound("fees.equals=" + UPDATED_FEES);
    }

    @Test
    @Transactional
    public void getAllReservationsByFeesIsNotEqualToSomething() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where fees not equals to DEFAULT_FEES
        defaultReservationShouldNotBeFound("fees.notEquals=" + DEFAULT_FEES);

        // Get all the reservationList where fees not equals to UPDATED_FEES
        defaultReservationShouldBeFound("fees.notEquals=" + UPDATED_FEES);
    }

    @Test
    @Transactional
    public void getAllReservationsByFeesIsInShouldWork() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where fees in DEFAULT_FEES or UPDATED_FEES
        defaultReservationShouldBeFound("fees.in=" + DEFAULT_FEES + "," + UPDATED_FEES);

        // Get all the reservationList where fees equals to UPDATED_FEES
        defaultReservationShouldNotBeFound("fees.in=" + UPDATED_FEES);
    }

    @Test
    @Transactional
    public void getAllReservationsByFeesIsNullOrNotNull() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where fees is not null
        defaultReservationShouldBeFound("fees.specified=true");

        // Get all the reservationList where fees is null
        defaultReservationShouldNotBeFound("fees.specified=false");
    }

    @Test
    @Transactional
    public void getAllReservationsByFeesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where fees is greater than or equal to DEFAULT_FEES
        defaultReservationShouldBeFound("fees.greaterThanOrEqual=" + DEFAULT_FEES);

        // Get all the reservationList where fees is greater than or equal to UPDATED_FEES
        defaultReservationShouldNotBeFound("fees.greaterThanOrEqual=" + UPDATED_FEES);
    }

    @Test
    @Transactional
    public void getAllReservationsByFeesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where fees is less than or equal to DEFAULT_FEES
        defaultReservationShouldBeFound("fees.lessThanOrEqual=" + DEFAULT_FEES);

        // Get all the reservationList where fees is less than or equal to SMALLER_FEES
        defaultReservationShouldNotBeFound("fees.lessThanOrEqual=" + SMALLER_FEES);
    }

    @Test
    @Transactional
    public void getAllReservationsByFeesIsLessThanSomething() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where fees is less than DEFAULT_FEES
        defaultReservationShouldNotBeFound("fees.lessThan=" + DEFAULT_FEES);

        // Get all the reservationList where fees is less than UPDATED_FEES
        defaultReservationShouldBeFound("fees.lessThan=" + UPDATED_FEES);
    }

    @Test
    @Transactional
    public void getAllReservationsByFeesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList where fees is greater than DEFAULT_FEES
        defaultReservationShouldNotBeFound("fees.greaterThan=" + DEFAULT_FEES);

        // Get all the reservationList where fees is greater than SMALLER_FEES
        defaultReservationShouldBeFound("fees.greaterThan=" + SMALLER_FEES);
    }


    @Test
    @Transactional
    public void getAllReservationsByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        reservation.setUser(user);
        reservationRepository.saveAndFlush(reservation);
        Long userId = user.getId();

        // Get all the reservationList where user equals to userId
        defaultReservationShouldBeFound("userId.equals=" + userId);

        // Get all the reservationList where user equals to userId + 1
        defaultReservationShouldNotBeFound("userId.equals=" + (userId + 1));
    }


    @Test
    @Transactional
    public void getAllReservationsByPlaceIsEqualToSomething() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);
        Place place = PlaceResourceIT.createEntity(em);
        em.persist(place);
        em.flush();
        reservation.setPlace(place);
        reservationRepository.saveAndFlush(reservation);
        Long placeId = place.getId();

        // Get all the reservationList where place equals to placeId
        defaultReservationShouldBeFound("placeId.equals=" + placeId);

        // Get all the reservationList where place equals to placeId + 1
        defaultReservationShouldNotBeFound("placeId.equals=" + (placeId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultReservationShouldBeFound(String filter) throws Exception {
        restReservationMockMvc.perform(get("/api/reservations?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reservation.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].fees").value(hasItem(DEFAULT_FEES.doubleValue())));

        // Check, that the count call also returns 1
        restReservationMockMvc.perform(get("/api/reservations/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultReservationShouldNotBeFound(String filter) throws Exception {
        restReservationMockMvc.perform(get("/api/reservations?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restReservationMockMvc.perform(get("/api/reservations/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingReservation() throws Exception {
        // Get the reservation
        restReservationMockMvc.perform(get("/api/reservations/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateReservation() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        int databaseSizeBeforeUpdate = reservationRepository.findAll().size();

        // Update the reservation
        Reservation updatedReservation = reservationRepository.findById(reservation.getId()).get();
        // Disconnect from session so that the updates on updatedReservation are not directly saved in db
        em.detach(updatedReservation);
        updatedReservation
            .type(UPDATED_TYPE)
            .status(UPDATED_STATUS)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .fees(UPDATED_FEES);
        ReservationDTO reservationDTO = reservationMapper.toDto(updatedReservation);

        restReservationMockMvc.perform(put("/api/reservations")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(reservationDTO)))
            .andExpect(status().isOk());

        // Validate the Reservation in the database
        List<Reservation> reservationList = reservationRepository.findAll();
        assertThat(reservationList).hasSize(databaseSizeBeforeUpdate);
        Reservation testReservation = reservationList.get(reservationList.size() - 1);
        assertThat(testReservation.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testReservation.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testReservation.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testReservation.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testReservation.getFees()).isEqualTo(UPDATED_FEES);
    }

    @Test
    @Transactional
    public void updateNonExistingReservation() throws Exception {
        int databaseSizeBeforeUpdate = reservationRepository.findAll().size();

        // Create the Reservation
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReservationMockMvc.perform(put("/api/reservations")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(reservationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Reservation in the database
        List<Reservation> reservationList = reservationRepository.findAll();
        assertThat(reservationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteReservation() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        int databaseSizeBeforeDelete = reservationRepository.findAll().size();

        // Delete the reservation
        restReservationMockMvc.perform(delete("/api/reservations/{id}", reservation.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Reservation> reservationList = reservationRepository.findAll();
        assertThat(reservationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
