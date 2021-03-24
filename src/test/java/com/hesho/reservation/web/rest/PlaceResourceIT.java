package com.hesho.reservation.web.rest;

import com.hesho.reservation.ReservationBackendApp;
import com.hesho.reservation.domain.Place;
import com.hesho.reservation.domain.Location;
import com.hesho.reservation.domain.Image;
import com.hesho.reservation.domain.Reservation;
import com.hesho.reservation.domain.Category;
import com.hesho.reservation.repository.PlaceRepository;
import com.hesho.reservation.service.PlaceService;
import com.hesho.reservation.service.dto.PlaceDTO;
import com.hesho.reservation.service.mapper.PlaceMapper;
import com.hesho.reservation.service.dto.PlaceCriteria;
import com.hesho.reservation.service.PlaceQueryService;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link PlaceResource} REST controller.
 */
@SpringBootTest(classes = ReservationBackendApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class PlaceResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SPECIFICATION = "AAAAAAAAAA";
    private static final String UPDATED_SPECIFICATION = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Double DEFAULT_PRICE = 1D;
    private static final Double UPDATED_PRICE = 2D;
    private static final Double SMALLER_PRICE = 1D - 1D;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private PlaceMapper placeMapper;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private PlaceQueryService placeQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPlaceMockMvc;

    private Place place;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Place createEntity(EntityManager em) {
        Place place = new Place()
            .name(DEFAULT_NAME)
            .specification(DEFAULT_SPECIFICATION)
            .description(DEFAULT_DESCRIPTION)
            .price(DEFAULT_PRICE);
        return place;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Place createUpdatedEntity(EntityManager em) {
        Place place = new Place()
            .name(UPDATED_NAME)
            .specification(UPDATED_SPECIFICATION)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE);
        return place;
    }

    @BeforeEach
    public void initTest() {
        place = createEntity(em);
    }

    @Test
    @Transactional
    public void createPlace() throws Exception {
        int databaseSizeBeforeCreate = placeRepository.findAll().size();
        // Create the Place
        PlaceDTO placeDTO = placeMapper.toDto(place);
        restPlaceMockMvc.perform(post("/api/places")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(placeDTO)))
            .andExpect(status().isCreated());

        // Validate the Place in the database
        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeCreate + 1);
        Place testPlace = placeList.get(placeList.size() - 1);
        assertThat(testPlace.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPlace.getSpecification()).isEqualTo(DEFAULT_SPECIFICATION);
        assertThat(testPlace.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPlace.getPrice()).isEqualTo(DEFAULT_PRICE);
    }

    @Test
    @Transactional
    public void createPlaceWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = placeRepository.findAll().size();

        // Create the Place with an existing ID
        place.setId(1L);
        PlaceDTO placeDTO = placeMapper.toDto(place);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlaceMockMvc.perform(post("/api/places")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(placeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Place in the database
        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = placeRepository.findAll().size();
        // set the field null
        place.setName(null);

        // Create the Place, which fails.
        PlaceDTO placeDTO = placeMapper.toDto(place);


        restPlaceMockMvc.perform(post("/api/places")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(placeDTO)))
            .andExpect(status().isBadRequest());

        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSpecificationIsRequired() throws Exception {
        int databaseSizeBeforeTest = placeRepository.findAll().size();
        // set the field null
        place.setSpecification(null);

        // Create the Place, which fails.
        PlaceDTO placeDTO = placeMapper.toDto(place);


        restPlaceMockMvc.perform(post("/api/places")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(placeDTO)))
            .andExpect(status().isBadRequest());

        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPlaces() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList
        restPlaceMockMvc.perform(get("/api/places?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(place.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].specification").value(hasItem(DEFAULT_SPECIFICATION)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())));
    }
    
    @Test
    @Transactional
    public void getPlace() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get the place
        restPlaceMockMvc.perform(get("/api/places/{id}", place.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(place.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.specification").value(DEFAULT_SPECIFICATION))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()));
    }


    @Test
    @Transactional
    public void getPlacesByIdFiltering() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        Long id = place.getId();

        defaultPlaceShouldBeFound("id.equals=" + id);
        defaultPlaceShouldNotBeFound("id.notEquals=" + id);

        defaultPlaceShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPlaceShouldNotBeFound("id.greaterThan=" + id);

        defaultPlaceShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPlaceShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllPlacesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where name equals to DEFAULT_NAME
        defaultPlaceShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the placeList where name equals to UPDATED_NAME
        defaultPlaceShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPlacesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where name not equals to DEFAULT_NAME
        defaultPlaceShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the placeList where name not equals to UPDATED_NAME
        defaultPlaceShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPlacesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where name in DEFAULT_NAME or UPDATED_NAME
        defaultPlaceShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the placeList where name equals to UPDATED_NAME
        defaultPlaceShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPlacesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where name is not null
        defaultPlaceShouldBeFound("name.specified=true");

        // Get all the placeList where name is null
        defaultPlaceShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllPlacesByNameContainsSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where name contains DEFAULT_NAME
        defaultPlaceShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the placeList where name contains UPDATED_NAME
        defaultPlaceShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPlacesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where name does not contain DEFAULT_NAME
        defaultPlaceShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the placeList where name does not contain UPDATED_NAME
        defaultPlaceShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllPlacesBySpecificationIsEqualToSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where specification equals to DEFAULT_SPECIFICATION
        defaultPlaceShouldBeFound("specification.equals=" + DEFAULT_SPECIFICATION);

        // Get all the placeList where specification equals to UPDATED_SPECIFICATION
        defaultPlaceShouldNotBeFound("specification.equals=" + UPDATED_SPECIFICATION);
    }

    @Test
    @Transactional
    public void getAllPlacesBySpecificationIsNotEqualToSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where specification not equals to DEFAULT_SPECIFICATION
        defaultPlaceShouldNotBeFound("specification.notEquals=" + DEFAULT_SPECIFICATION);

        // Get all the placeList where specification not equals to UPDATED_SPECIFICATION
        defaultPlaceShouldBeFound("specification.notEquals=" + UPDATED_SPECIFICATION);
    }

    @Test
    @Transactional
    public void getAllPlacesBySpecificationIsInShouldWork() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where specification in DEFAULT_SPECIFICATION or UPDATED_SPECIFICATION
        defaultPlaceShouldBeFound("specification.in=" + DEFAULT_SPECIFICATION + "," + UPDATED_SPECIFICATION);

        // Get all the placeList where specification equals to UPDATED_SPECIFICATION
        defaultPlaceShouldNotBeFound("specification.in=" + UPDATED_SPECIFICATION);
    }

    @Test
    @Transactional
    public void getAllPlacesBySpecificationIsNullOrNotNull() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where specification is not null
        defaultPlaceShouldBeFound("specification.specified=true");

        // Get all the placeList where specification is null
        defaultPlaceShouldNotBeFound("specification.specified=false");
    }
                @Test
    @Transactional
    public void getAllPlacesBySpecificationContainsSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where specification contains DEFAULT_SPECIFICATION
        defaultPlaceShouldBeFound("specification.contains=" + DEFAULT_SPECIFICATION);

        // Get all the placeList where specification contains UPDATED_SPECIFICATION
        defaultPlaceShouldNotBeFound("specification.contains=" + UPDATED_SPECIFICATION);
    }

    @Test
    @Transactional
    public void getAllPlacesBySpecificationNotContainsSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where specification does not contain DEFAULT_SPECIFICATION
        defaultPlaceShouldNotBeFound("specification.doesNotContain=" + DEFAULT_SPECIFICATION);

        // Get all the placeList where specification does not contain UPDATED_SPECIFICATION
        defaultPlaceShouldBeFound("specification.doesNotContain=" + UPDATED_SPECIFICATION);
    }


    @Test
    @Transactional
    public void getAllPlacesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where description equals to DEFAULT_DESCRIPTION
        defaultPlaceShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the placeList where description equals to UPDATED_DESCRIPTION
        defaultPlaceShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllPlacesByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where description not equals to DEFAULT_DESCRIPTION
        defaultPlaceShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the placeList where description not equals to UPDATED_DESCRIPTION
        defaultPlaceShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllPlacesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultPlaceShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the placeList where description equals to UPDATED_DESCRIPTION
        defaultPlaceShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllPlacesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where description is not null
        defaultPlaceShouldBeFound("description.specified=true");

        // Get all the placeList where description is null
        defaultPlaceShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllPlacesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where description contains DEFAULT_DESCRIPTION
        defaultPlaceShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the placeList where description contains UPDATED_DESCRIPTION
        defaultPlaceShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllPlacesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where description does not contain DEFAULT_DESCRIPTION
        defaultPlaceShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the placeList where description does not contain UPDATED_DESCRIPTION
        defaultPlaceShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllPlacesByPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where price equals to DEFAULT_PRICE
        defaultPlaceShouldBeFound("price.equals=" + DEFAULT_PRICE);

        // Get all the placeList where price equals to UPDATED_PRICE
        defaultPlaceShouldNotBeFound("price.equals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    public void getAllPlacesByPriceIsNotEqualToSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where price not equals to DEFAULT_PRICE
        defaultPlaceShouldNotBeFound("price.notEquals=" + DEFAULT_PRICE);

        // Get all the placeList where price not equals to UPDATED_PRICE
        defaultPlaceShouldBeFound("price.notEquals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    public void getAllPlacesByPriceIsInShouldWork() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where price in DEFAULT_PRICE or UPDATED_PRICE
        defaultPlaceShouldBeFound("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE);

        // Get all the placeList where price equals to UPDATED_PRICE
        defaultPlaceShouldNotBeFound("price.in=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    public void getAllPlacesByPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where price is not null
        defaultPlaceShouldBeFound("price.specified=true");

        // Get all the placeList where price is null
        defaultPlaceShouldNotBeFound("price.specified=false");
    }

    @Test
    @Transactional
    public void getAllPlacesByPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where price is greater than or equal to DEFAULT_PRICE
        defaultPlaceShouldBeFound("price.greaterThanOrEqual=" + DEFAULT_PRICE);

        // Get all the placeList where price is greater than or equal to UPDATED_PRICE
        defaultPlaceShouldNotBeFound("price.greaterThanOrEqual=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    public void getAllPlacesByPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where price is less than or equal to DEFAULT_PRICE
        defaultPlaceShouldBeFound("price.lessThanOrEqual=" + DEFAULT_PRICE);

        // Get all the placeList where price is less than or equal to SMALLER_PRICE
        defaultPlaceShouldNotBeFound("price.lessThanOrEqual=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    public void getAllPlacesByPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where price is less than DEFAULT_PRICE
        defaultPlaceShouldNotBeFound("price.lessThan=" + DEFAULT_PRICE);

        // Get all the placeList where price is less than UPDATED_PRICE
        defaultPlaceShouldBeFound("price.lessThan=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    public void getAllPlacesByPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where price is greater than DEFAULT_PRICE
        defaultPlaceShouldNotBeFound("price.greaterThan=" + DEFAULT_PRICE);

        // Get all the placeList where price is greater than SMALLER_PRICE
        defaultPlaceShouldBeFound("price.greaterThan=" + SMALLER_PRICE);
    }


    @Test
    @Transactional
    public void getAllPlacesByLocationIsEqualToSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);
        Location location = LocationResourceIT.createEntity(em);
        em.persist(location);
        em.flush();
        place.setLocation(location);
        placeRepository.saveAndFlush(place);
        Long locationId = location.getId();

        // Get all the placeList where location equals to locationId
        defaultPlaceShouldBeFound("locationId.equals=" + locationId);

        // Get all the placeList where location equals to locationId + 1
        defaultPlaceShouldNotBeFound("locationId.equals=" + (locationId + 1));
    }


    @Test
    @Transactional
    public void getAllPlacesByImagesIsEqualToSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);
        Image images = ImageResourceIT.createEntity(em);
        em.persist(images);
        em.flush();
        place.addImages(images);
        placeRepository.saveAndFlush(place);
        Long imagesId = images.getId();

        // Get all the placeList where images equals to imagesId
        defaultPlaceShouldBeFound("imagesId.equals=" + imagesId);

        // Get all the placeList where images equals to imagesId + 1
        defaultPlaceShouldNotBeFound("imagesId.equals=" + (imagesId + 1));
    }


    @Test
    @Transactional
    public void getAllPlacesByReservationsIsEqualToSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);
        Reservation reservations = ReservationResourceIT.createEntity(em);
        em.persist(reservations);
        em.flush();
        place.addReservations(reservations);
        placeRepository.saveAndFlush(place);
        Long reservationsId = reservations.getId();

        // Get all the placeList where reservations equals to reservationsId
        defaultPlaceShouldBeFound("reservationsId.equals=" + reservationsId);

        // Get all the placeList where reservations equals to reservationsId + 1
        defaultPlaceShouldNotBeFound("reservationsId.equals=" + (reservationsId + 1));
    }


    @Test
    @Transactional
    public void getAllPlacesByCategoryIsEqualToSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);
        Category category = CategoryResourceIT.createEntity(em);
        em.persist(category);
        em.flush();
        place.setCategory(category);
        placeRepository.saveAndFlush(place);
        Long categoryId = category.getId();

        // Get all the placeList where category equals to categoryId
        defaultPlaceShouldBeFound("categoryId.equals=" + categoryId);

        // Get all the placeList where category equals to categoryId + 1
        defaultPlaceShouldNotBeFound("categoryId.equals=" + (categoryId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPlaceShouldBeFound(String filter) throws Exception {
        restPlaceMockMvc.perform(get("/api/places?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(place.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].specification").value(hasItem(DEFAULT_SPECIFICATION)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())));

        // Check, that the count call also returns 1
        restPlaceMockMvc.perform(get("/api/places/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPlaceShouldNotBeFound(String filter) throws Exception {
        restPlaceMockMvc.perform(get("/api/places?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPlaceMockMvc.perform(get("/api/places/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingPlace() throws Exception {
        // Get the place
        restPlaceMockMvc.perform(get("/api/places/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePlace() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        int databaseSizeBeforeUpdate = placeRepository.findAll().size();

        // Update the place
        Place updatedPlace = placeRepository.findById(place.getId()).get();
        // Disconnect from session so that the updates on updatedPlace are not directly saved in db
        em.detach(updatedPlace);
        updatedPlace
            .name(UPDATED_NAME)
            .specification(UPDATED_SPECIFICATION)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE);
        PlaceDTO placeDTO = placeMapper.toDto(updatedPlace);

        restPlaceMockMvc.perform(put("/api/places")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(placeDTO)))
            .andExpect(status().isOk());

        // Validate the Place in the database
        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeUpdate);
        Place testPlace = placeList.get(placeList.size() - 1);
        assertThat(testPlace.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPlace.getSpecification()).isEqualTo(UPDATED_SPECIFICATION);
        assertThat(testPlace.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPlace.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    @Transactional
    public void updateNonExistingPlace() throws Exception {
        int databaseSizeBeforeUpdate = placeRepository.findAll().size();

        // Create the Place
        PlaceDTO placeDTO = placeMapper.toDto(place);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlaceMockMvc.perform(put("/api/places")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(placeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Place in the database
        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deletePlace() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        int databaseSizeBeforeDelete = placeRepository.findAll().size();

        // Delete the place
        restPlaceMockMvc.perform(delete("/api/places/{id}", place.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
