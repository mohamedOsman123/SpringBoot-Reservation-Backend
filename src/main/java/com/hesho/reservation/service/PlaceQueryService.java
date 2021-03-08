package com.hesho.reservation.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.hesho.reservation.domain.Place;
import com.hesho.reservation.domain.*; // for static metamodels
import com.hesho.reservation.repository.PlaceRepository;
import com.hesho.reservation.service.dto.PlaceCriteria;
import com.hesho.reservation.service.dto.PlaceDTO;
import com.hesho.reservation.service.mapper.PlaceMapper;

/**
 * Service for executing complex queries for {@link Place} entities in the database.
 * The main input is a {@link PlaceCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PlaceDTO} or a {@link Page} of {@link PlaceDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PlaceQueryService extends QueryService<Place> {

    private final Logger log = LoggerFactory.getLogger(PlaceQueryService.class);

    private final PlaceRepository placeRepository;

    private final PlaceMapper placeMapper;

    public PlaceQueryService(PlaceRepository placeRepository, PlaceMapper placeMapper) {
        this.placeRepository = placeRepository;
        this.placeMapper = placeMapper;
    }

    /**
     * Return a {@link List} of {@link PlaceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PlaceDTO> findByCriteria(PlaceCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Place> specification = createSpecification(criteria);
        return placeMapper.toDto(placeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link PlaceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PlaceDTO> findByCriteria(PlaceCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Place> specification = createSpecification(criteria);
        return placeRepository.findAll(specification, page)
            .map(placeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PlaceCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Place> specification = createSpecification(criteria);
        return placeRepository.count(specification);
    }

    /**
     * Function to convert {@link PlaceCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Place> createSpecification(PlaceCriteria criteria) {
        Specification<Place> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Place_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Place_.name));
            }
            if (criteria.getSpecification() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSpecification(), Place_.specification));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Place_.description));
            }
            if (criteria.getLocationId() != null) {
                specification = specification.and(buildSpecification(criteria.getLocationId(),
                    root -> root.join(Place_.location, JoinType.LEFT).get(Location_.id)));
            }
            if (criteria.getImagesId() != null) {
                specification = specification.and(buildSpecification(criteria.getImagesId(),
                    root -> root.join(Place_.images, JoinType.LEFT).get(Image_.id)));
            }
            if (criteria.getReservationsId() != null) {
                specification = specification.and(buildSpecification(criteria.getReservationsId(),
                    root -> root.join(Place_.reservations, JoinType.LEFT).get(Reservation_.id)));
            }
            if (criteria.getCategoryId() != null) {
                specification = specification.and(buildSpecification(criteria.getCategoryId(),
                    root -> root.join(Place_.category, JoinType.LEFT).get(Category_.id)));
            }
        }
        return specification;
    }
}
