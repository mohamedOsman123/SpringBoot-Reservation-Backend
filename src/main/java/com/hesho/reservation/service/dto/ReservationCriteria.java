package com.hesho.reservation.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import com.hesho.reservation.domain.enumeration.ReservationType;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the {@link com.hesho.reservation.domain.Reservation} entity. This class is used
 * in {@link com.hesho.reservation.web.rest.ReservationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /reservations?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ReservationCriteria implements Serializable, Criteria {
    /**
     * Class for filtering ReservationType
     */
    public static class ReservationTypeFilter extends Filter<ReservationType> {

        public ReservationTypeFilter() {
        }

        public ReservationTypeFilter(ReservationTypeFilter filter) {
            super(filter);
        }

        @Override
        public ReservationTypeFilter copy() {
            return new ReservationTypeFilter(this);
        }

    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ReservationTypeFilter type;

    private IntegerFilter period;

    private InstantFilter startDate;

    private InstantFilter endDate;

    private LongFilter userId;

    private LongFilter placeId;

    public ReservationCriteria() {
    }

    public ReservationCriteria(ReservationCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.period = other.period == null ? null : other.period.copy();
        this.startDate = other.startDate == null ? null : other.startDate.copy();
        this.endDate = other.endDate == null ? null : other.endDate.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.placeId = other.placeId == null ? null : other.placeId.copy();
    }

    @Override
    public ReservationCriteria copy() {
        return new ReservationCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public ReservationTypeFilter getType() {
        return type;
    }

    public void setType(ReservationTypeFilter type) {
        this.type = type;
    }

    public IntegerFilter getPeriod() {
        return period;
    }

    public void setPeriod(IntegerFilter period) {
        this.period = period;
    }

    public InstantFilter getStartDate() {
        return startDate;
    }

    public void setStartDate(InstantFilter startDate) {
        this.startDate = startDate;
    }

    public InstantFilter getEndDate() {
        return endDate;
    }

    public void setEndDate(InstantFilter endDate) {
        this.endDate = endDate;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public LongFilter getPlaceId() {
        return placeId;
    }

    public void setPlaceId(LongFilter placeId) {
        this.placeId = placeId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ReservationCriteria that = (ReservationCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(type, that.type) &&
            Objects.equals(period, that.period) &&
            Objects.equals(startDate, that.startDate) &&
            Objects.equals(endDate, that.endDate) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(placeId, that.placeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        type,
        period,
        startDate,
        endDate,
        userId,
        placeId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReservationCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (type != null ? "type=" + type + ", " : "") +
                (period != null ? "period=" + period + ", " : "") +
                (startDate != null ? "startDate=" + startDate + ", " : "") +
                (endDate != null ? "endDate=" + endDate + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
                (placeId != null ? "placeId=" + placeId + ", " : "") +
            "}";
    }

}
