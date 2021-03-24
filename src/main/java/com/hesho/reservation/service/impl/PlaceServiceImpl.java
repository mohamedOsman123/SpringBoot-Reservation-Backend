package com.hesho.reservation.service.impl;

import com.hesho.reservation.domain.Location;
import com.hesho.reservation.repository.LocationRepository;
import com.hesho.reservation.service.PlaceService;
import com.hesho.reservation.domain.Place;
import com.hesho.reservation.repository.PlaceRepository;
import com.hesho.reservation.service.dto.PlaceDTO;
import com.hesho.reservation.service.mapper.PlaceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Place}.
 */
@Service
@Transactional
public class PlaceServiceImpl implements PlaceService {

    private final Logger log = LoggerFactory.getLogger(PlaceServiceImpl.class);

    private final PlaceRepository placeRepository;

    private final PlaceMapper placeMapper;

    @Autowired
    private LocationRepository locationRepository;

    public PlaceServiceImpl(PlaceRepository placeRepository, PlaceMapper placeMapper) {
        this.placeRepository = placeRepository;
        this.placeMapper = placeMapper;
    }

    @Override
    public PlaceDTO save(PlaceDTO placeDTO) {
        log.debug("Request to save Place : {}", placeDTO);
        Location location=new Location();
        location.setAddress(placeDTO.getAddress());
        location.setCity(placeDTO.getCity());
        location.setLatitude(placeDTO.getLatitude());
        location.setLongitude(placeDTO.getLongitude());
        Location locationEntity=locationRepository.save(location);
        Place place = placeMapper.toEntity(placeDTO);
        place.setLocation(locationEntity);
        place = placeRepository.save(place);
        return placeMapper.toDto(place);
    }

    @Override
    public PlaceDTO update(PlaceDTO placeDTO) {
        log.debug("Request to update Place : {}", placeDTO);
        Location location= locationRepository.findByPlaceId(placeDTO.getId());
        if (placeDTO.getAddress()!=null)
        location.setAddress(placeDTO.getAddress());

        if (placeDTO.getCity()!=null)
        location.setCity(placeDTO.getCity());

        if (placeDTO.getLatitude()!=null)
        location.setLatitude(placeDTO.getLatitude());

        if (placeDTO.getLongitude()!=null)
        location.setLongitude(placeDTO.getLongitude());

        locationRepository.save(location);
        placeDTO.setLocationId(location.getId());
        Place placeEntity = placeMapper.toEntity(placeDTO);
        Place place = placeRepository.save(placeEntity);
        return placeMapper.toDto(place);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PlaceDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Places");
        return placeRepository.findAll(pageable)
            .map(placeMapper::toDto);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<PlaceDTO> findOne(Long id) {
        log.debug("Request to get Place : {}", id);
        return placeRepository.findById(id)
            .map(placeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Place : {}", id);
        placeRepository.deleteById(id);
    }
}
