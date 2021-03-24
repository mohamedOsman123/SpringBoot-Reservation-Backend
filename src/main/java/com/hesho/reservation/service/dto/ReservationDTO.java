package com.hesho.reservation.service.dto;

import java.time.Instant;
import java.io.Serializable;
import com.hesho.reservation.domain.enumeration.ReservationType;
import com.hesho.reservation.domain.enumeration.ReservationStatus;

/**
 * A DTO for the {@link com.hesho.reservation.domain.Reservation} entity.
 */
public class ReservationDTO implements Serializable {
    
    private Long id;

    private ReservationType type;

    private ReservationStatus status;

    private Instant startDate;

    private Instant endDate;

    private Double fees;


    private Long userId;

    private String userLogin;

    private Long placeId;

    private String placeName;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReservationType getType() {
        return type;
    }

    public void setType(ReservationType type) {
        this.type = type;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public Double getFees() {
        return fees;
    }

    public void setFees(Double fees) {
        this.fees = fees;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReservationDTO)) {
            return false;
        }

        return id != null && id.equals(((ReservationDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReservationDTO{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", status='" + getStatus() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", fees=" + getFees() +
            ", userId=" + getUserId() +
            ", userLogin='" + getUserLogin() + "'" +
            ", placeId=" + getPlaceId() +
            ", placeName='" + getPlaceName() + "'" +
            "}";
    }
}
