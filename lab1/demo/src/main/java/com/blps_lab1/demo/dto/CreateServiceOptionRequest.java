package com.blps_lab1.demo.dto;

public class CreateServiceOptionRequest {
    private String name;
    private String description;
    private double pricePerDay;
    private Boolean petRelated;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public Boolean getPetRelated() {
        return petRelated;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public void setPetRelated(Boolean petRelated) {
        this.petRelated = petRelated;
    }
}
