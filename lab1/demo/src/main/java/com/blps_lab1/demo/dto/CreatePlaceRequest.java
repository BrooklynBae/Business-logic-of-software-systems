package com.blps_lab1.demo.dto;

import com.blps_lab1.demo.data.tables.PlaceType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreatePlaceRequest {

    @NotBlank(message = "Town cannot be empty or null")
    private String town;

    @NotBlank(message = "Name cannot be empty or null")
    private String name;

    private String description;

    @NotNull(message = "Place type cannot be null")
    private PlaceType placeType;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price per night must be greater than 0")
    private double pricePerNight;

    @Min(value = 1, message = "Max guests must be at least 1")
    private int maxGuests;

    private double rating;
    private Boolean petsAllowed;

    @NotNull(message = "Owner ID cannot be null")
    private Long ownerId;

    public CreatePlaceRequest() {
    }

    public String getTown() {
        return town;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public PlaceType getPlaceType() {
        return placeType;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public int getMaxGuests() {
        return maxGuests;
    }

    public double getRating() {
        return rating;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public Boolean getPetsAllowed() {
        return petsAllowed;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPlaceType(PlaceType placeType) {
        this.placeType = placeType;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public void setMaxGuests(int maxGuests) {
        this.maxGuests = maxGuests;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public void setPetsAllowed(Boolean petsAllowed) {
        this.petsAllowed = petsAllowed;
    }
}
