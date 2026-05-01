package com.blps_lab1.demo.dto;

import java.time.LocalDate;
import java.util.List;

public class ReservationRequest {
    private LocalDate arrival;
    private LocalDate departure;
    private Integer guestsAmount;
    private Boolean agreedToReservation;
    private Integer petsAmount;
    private Long userId;
    private Long idPlace;
    private List<Long> serviceOptionIds;

    public Long getIdPlace() {
        return idPlace;
    }

    public void setIdPlace(Long idPlace) {
        this.idPlace = idPlace;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getPetsAmount() {
        return petsAmount;
    }

    public void setPetsAmount(Integer petsAmount) {
        this.petsAmount = petsAmount;
    }

    public ReservationRequest() {
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

    public Boolean getAgreedToReservation() {
        return agreedToReservation;
    }

    public void setAgreedToReservation(Boolean agreedToReservation) {
        this.agreedToReservation = agreedToReservation;
    }

    public List<Long> getServiceOptionIds() {
        return serviceOptionIds;
    }

    public void setServiceOptionIds(List<Long> serviceOptionIds) {
        this.serviceOptionIds = serviceOptionIds;
    }
}