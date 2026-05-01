package com.blps_lab1.demo.dto;

import com.blps_lab1.demo.data.tables.PaymentMethod;
import com.blps_lab1.demo.data.tables.PaymentType;

import java.time.LocalDate;
import java.util.List;

public class CreateReservationEntityRequest {
    private Long placeId;
    private Long userId;
    private LocalDate arrival;
    private LocalDate departure;
    private Integer guestsAmount;
    private Integer petsAmount;
    private PaymentType paymentType;
    private PaymentMethod paymentMethod;
    private List<Long> serviceOptionIds;

    public CreateReservationEntityRequest() {
    }

    public Long getPlaceId() {
        return placeId;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDate getArrival() {
        return arrival;
    }

    public LocalDate getDeparture() {
        return departure;
    }

    public Integer getGuestsAmount() {
        return guestsAmount;
    }

    public Integer getPetsAmount() {
        return petsAmount;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public List<Long> getServiceOptionIds() {
        return serviceOptionIds;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setArrival(LocalDate arrival) {
        this.arrival = arrival;
    }

    public void setDeparture(LocalDate departure) {
        this.departure = departure;
    }

    public void setGuestsAmount(Integer guestsAmount) {
        this.guestsAmount = guestsAmount;
    }

    public void setPetsAmount(Integer petsAmount) {
        this.petsAmount = petsAmount;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setServiceOptionIds(List<Long> serviceOptionIds) {
        this.serviceOptionIds = serviceOptionIds;
    }
}