package com.hesho.reservation.service.impl;

import com.hesho.reservation.domain.enumeration.ReservationStatus;
import com.hesho.reservation.service.ReservationService;
import com.hesho.reservation.domain.Reservation;
import com.hesho.reservation.repository.ReservationRepository;
import com.hesho.reservation.service.dto.ReservationDTO;
import com.hesho.reservation.service.mapper.ReservationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Reservation}.
 */
@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final Logger log = LoggerFactory.getLogger(ReservationServiceImpl.class);

    private final ReservationRepository reservationRepository;

    private final ReservationMapper reservationMapper;

    public ReservationServiceImpl(ReservationRepository reservationRepository, ReservationMapper reservationMapper) {
        this.reservationRepository = reservationRepository;
        this.reservationMapper = reservationMapper;
    }

    @Override
    public ReservationDTO save(ReservationDTO reservationDTO) {
        log.debug("Request to save Reservation : {}", reservationDTO);
        Reservation reservation = reservationMapper.toEntity(reservationDTO);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation = reservationRepository.save(reservation);
        return reservationMapper.toDto(reservation);
    }

    @Override
    public ReservationDTO updateStatus(Long id,ReservationStatus status) {
        log.debug("Request to update Reservation Status: {}", id);
        Reservation reservation = reservationRepository.getOne(id);
        if (reservation!=null) {
            reservation.setStatus(status);
            reservation = reservationRepository.save(reservation);
            return reservationMapper.toDto(reservation);
        }
        else{
            throw new UsernameNotFoundException(id.toString());
        }
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ReservationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Reservations");
        return reservationRepository.findAll(pageable)
            .map(reservationMapper::toDto);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<ReservationDTO> findOne(Long id) {
        log.debug("Request to get Reservation : {}", id);
        return reservationRepository.findById(id)
            .map(reservationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Reservation : {}", id);
        reservationRepository.deleteById(id);
    }
}
