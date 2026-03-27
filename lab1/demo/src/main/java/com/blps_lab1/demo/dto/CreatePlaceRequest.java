package com.blps_lab1.demo.dto;

import com.blps_lab1.demo.data.tables.PlaceType;

public class CreatePlaceRequest {
    private String town;
    private String name;
    private String description;
    private PlaceType placeType;
    private double pricePerNight;
    private int maxGuests;
    private double rating;
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
}