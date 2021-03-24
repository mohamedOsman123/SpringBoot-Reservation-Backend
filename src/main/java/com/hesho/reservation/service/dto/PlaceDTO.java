package com.hesho.reservation.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * A DTO for the {@link com.hesho.reservation.domain.Place} entity.
 */
public class PlaceDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String specification;

    private String description;

    private Double price;
    
    private Long locationId;

    private Long categoryId;

    private String categoryName;

    @NotNull
    private String address;

    private String latitude;

    private String longitude;

    @NotNull
    private String city;


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlaceDTO)) {
            return false;
        }

        return id != null && id.equals(((PlaceDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "PlaceDTO{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", specification='" + specification + '\'' +
            ", description='" + description + '\'' +
            ", price=" + price +
            ", locationId=" + locationId +
            ", categoryId=" + categoryId +
            ", categoryName='" + categoryName + '\'' +
            ", address='" + address + '\'' +
            ", latitude='" + latitude + '\'' +
            ", longitude='" + longitude + '\'' +
            ", city='" + city + '\'' +
            '}';
    }

}
