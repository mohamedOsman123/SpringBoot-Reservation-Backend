package com.hesho.reservation.service;

import com.hesho.reservation.domain.Place;
import com.hesho.reservation.service.dto.PlaceDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.hesho.reservation.domain.Place}.
 */
public interface PlaceService {

    /**
     * Save a place.
     *
     * @param placeDTO the entity to save.
     * @return the persisted entity.
     */
    PlaceDTO save(PlaceDTO placeDTO);

    PlaceDTO update(PlaceDTO placeDTO);

    /**
     * Get all the places.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PlaceDTO> findAll(Pageable pageable);


    /**
     * Get the "id" place.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PlaceDTO> findOne(Long id);

    /**
     * Delete the "id" place.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    Optional<Place> findPlaceByImageId(Long imageId);
}
