package com.blps_lab1.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class ReservationRequest {

    @NotNull(message = "Arrival date cannot be null")
    private LocalDate arrival;

    @NotNull(message = "Departure date cannot be null")
    private LocalDate departure;

    @NotNull(message = "Guests amount cannot be null")
    @Min(value = 1, message = "Guests amount must be at least 1")
    private Integer guestsAmount;

    private Boolean agreedToReservation;

    @Min(value = 0, message = "Pets amount cannot be negative")
    private Integer petsAmount;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotNull(message = "Place ID cannot be null")
    private Long idPlace;

    private List<Long> serviceOptionIds;
    private String coverLetter;

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

    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }
}
