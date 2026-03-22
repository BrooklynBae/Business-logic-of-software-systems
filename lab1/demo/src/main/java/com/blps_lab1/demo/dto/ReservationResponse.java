package com.blps_lab1.demo.dto;

import java.time.LocalDate;

public class ReservationResponse {
    private Long id;
    private LocalDate arrival;
    private LocalDate departure;
    private Integer guestsAmount;
    private Integer petsAmount;
    private Double price;
    private Long idPlace;

    public ReservationResponse(Long id, LocalDate arrival, LocalDate departure, Integer guestsAmount, Integer petsAmount, Double price, Long idPlace) {
        this.id = id;
        this.arrival = arrival;
        this.departure = departure;
        this.guestsAmount = guestsAmount;
        this.petsAmount = petsAmount;
        this.price = price;
        this.idPlace = idPlace;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getArrival() {
        return arrival;
    }

    public void setArrival(LocalDate arrival) {
        this.arrival = arrival;
    }

    public LocalDate getDeparture() {
        return departure;
    }

    public void setDeparture(LocalDate departure) {
        this.departure = departure;
    }

    public Integer getGuestsAmount() {
        return guestsAmount;
    }

    public void setGuestsAmount(Integer guestsAmount) {
        this.guestsAmount = guestsAmount;
    }

    public Integer getPetsAmount() {
        return petsAmount;
    }

    public void setPetsAmount(Integer petsAmount) {
        this.petsAmount = petsAmount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getIdPlace() {
        return idPlace;
    }

    public void setIdPlace(Long idPlace) {
        this.idPlace = idPlace;
    }
}
