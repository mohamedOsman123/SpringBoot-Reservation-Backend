package com.hesho.reservation.service.mapper;


import com.hesho.reservation.domain.*;
import com.hesho.reservation.service.dto.PlaceDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Place} and its DTO {@link PlaceDTO}.
 */
@Mapper(componentModel = "spring", uses = {LocationMapper.class, CategoryMapper.class})
public interface PlaceMapper extends EntityMapper<PlaceDTO, Place> {

    @Mapping(source = "location.id", target = "locationId")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    PlaceDTO toDto(Place place);

    @Mapping(source = "locationId", target = "location")
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "removeImages", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    @Mapping(target = "removeReservations", ignore = true)
    @Mapping(source = "categoryId", target = "category")
    Place toEntity(PlaceDTO placeDTO);

    default Place fromId(Long id) {
        if (id == null) {
            return null;
        }
        Place place = new Place();
        place.setId(id);
        return place;
    }
}
