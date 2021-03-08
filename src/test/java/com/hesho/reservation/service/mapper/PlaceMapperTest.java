package com.hesho.reservation.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class PlaceMapperTest {

    private PlaceMapper placeMapper;

    @BeforeEach
    public void setUp() {
        placeMapper = new PlaceMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(placeMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(placeMapper.fromId(null)).isNull();
    }
}
