package com.blps_lab1.demo.dto;

import com.blps_lab1.demo.data.tables.Owner;
import com.blps_lab1.demo.data.tables.PlaceType;

import java.util.List;

public class PlaceDto {
    public PlaceDto(Long id, String name, String town, String description, PlaceType placeType, double rating, Owner owner, List<DateDto> availableDates) {
        this.id = id;
        this.name = name;
        this.town = town;
        this.description = description;
        this.placeType = placeType;
        this.rating = rating;
        this.owner = owner;
        this.availableDates = availableDates;
    }

    private Long id;
    private String name;
    private String town;
    private String description;
    private PlaceType placeType;
    private double rating;
    private Owner owner;
    private List<DateDto> availableDates;
    public List<DateDto> getReservedDates() {
        return availableDates;
    }

    public void setAvailableDates(List<DateDto> availableDates) {
        this.availableDates = availableDates;
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

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PlaceType getPlaceType() {
        return placeType;
    }

    public void setPlaceType(PlaceType placeType) {
        this.placeType = placeType;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }
}
