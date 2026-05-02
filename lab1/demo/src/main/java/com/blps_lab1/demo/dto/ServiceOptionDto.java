package com.blps_lab1.demo.dto;

public class ServiceOptionDto {
    public static Builder builder() {
        return new Builder();
    }

    private Long id;
    private String name;
    private String description;
    private double pricePerDay;
    private Boolean petRelated;

    public ServiceOptionDto(Long id, String name, String description, double pricePerDay, Boolean petRelated) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.pricePerDay = pricePerDay;
        this.petRelated = petRelated;
    }

    public Long getId() {
        return id;
    }

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

    public static class Builder {
        private Long id;
        private String name;
        private String description;
        private double pricePerDay;
        private Boolean petRelated;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder pricePerDay(double pricePerDay) {
            this.pricePerDay = pricePerDay;
            return this;
        }

        public Builder petRelated(Boolean petRelated) {
            this.petRelated = petRelated;
            return this;
        }

        public ServiceOptionDto build() {
            return new ServiceOptionDto(id, name, description, pricePerDay, petRelated);
        }
    }
}
