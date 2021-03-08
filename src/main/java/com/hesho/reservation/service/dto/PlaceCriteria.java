package com.hesho.reservation.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.hesho.reservation.domain.Place} entity. This class is used
 * in {@link com.hesho.reservation.web.rest.PlaceResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /places?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class PlaceCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter specification;

    private StringFilter description;

    private LongFilter locationId;

    private LongFilter imagesId;

    private LongFilter reservationsId;

    private LongFilter categoryId;

    public PlaceCriteria() {
    }

    public PlaceCriteria(PlaceCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.specification = other.specification == null ? null : other.specification.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.locationId = other.locationId == null ? null : other.locationId.copy();
        this.imagesId = other.imagesId == null ? null : other.imagesId.copy();
        this.reservationsId = other.reservationsId == null ? null : other.reservationsId.copy();
        this.categoryId = other.categoryId == null ? null : other.categoryId.copy();
    }

    @Override
    public PlaceCriteria copy() {
        return new PlaceCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getSpecification() {
        return specification;
    }

    public void setSpecification(StringFilter specification) {
        this.specification = specification;
    }

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public LongFilter getLocationId() {
        return locationId;
    }

    public void setLocationId(LongFilter locationId) {
        this.locationId = locationId;
    }

    public LongFilter getImagesId() {
        return imagesId;
    }

    public void setImagesId(LongFilter imagesId) {
        this.imagesId = imagesId;
    }

    public LongFilter getReservationsId() {
        return reservationsId;
    }

    public void setReservationsId(LongFilter reservationsId) {
        this.reservationsId = reservationsId;
    }

    public LongFilter getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(LongFilter categoryId) {
        this.categoryId = categoryId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PlaceCriteria that = (PlaceCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(specification, that.specification) &&
            Objects.equals(description, that.description) &&
            Objects.equals(locationId, that.locationId) &&
            Objects.equals(imagesId, that.imagesId) &&
            Objects.equals(reservationsId, that.reservationsId) &&
            Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        name,
        specification,
        description,
        locationId,
        imagesId,
        reservationsId,
        categoryId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlaceCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (specification != null ? "specification=" + specification + ", " : "") +
                (description != null ? "description=" + description + ", " : "") +
                (locationId != null ? "locationId=" + locationId + ", " : "") +
                (imagesId != null ? "imagesId=" + imagesId + ", " : "") +
                (reservationsId != null ? "reservationsId=" + reservationsId + ", " : "") +
                (categoryId != null ? "categoryId=" + categoryId + ", " : "") +
            "}";
    }

}
