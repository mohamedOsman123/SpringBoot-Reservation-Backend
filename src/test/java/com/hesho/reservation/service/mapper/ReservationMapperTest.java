package com.hesho.reservation.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class ReservationMapperTest {

    private ReservationMapper reservationMapper;

    @BeforeEach
    public void setUp() {
        reservationMapper = new ReservationMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(reservationMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(reservationMapper.fromId(null)).isNull();
    }
}
