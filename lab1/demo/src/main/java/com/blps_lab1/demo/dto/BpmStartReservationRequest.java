package com.blps_lab1.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class BpmStartReservationRequest {
    private String town;
    @NotNull
    private LocalDate arrival;
    @NotNull
    private LocalDate departure;
    @NotNull
    @Min(1)
    private Integer guestsAmount;
    @Min(0)
    private Integer petsAmount;
    @NotNull
    private Long userId;
    private Long idPlace;
    private String coverLetter;

    public Map<String, Object> toVariables() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("town", town);
        variables.put("arrival", arrival);
        variables.put("departure", departure);
        variables.put("guestsAmount", guestsAmount);
        variables.put("petsAmount", petsAmount == null ? 0 : petsAmount);
        variables.put("userId", userId);
        if (idPlace != null) {
            variables.put("idPlace", idPlace);
        }
        if (coverLetter != null) {
            variables.put("coverLetter", coverLetter);
        }
        return variables;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getIdPlace() {
        return idPlace;
    }

    public void setIdPlace(Long idPlace) {
        this.idPlace = idPlace;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }
}
