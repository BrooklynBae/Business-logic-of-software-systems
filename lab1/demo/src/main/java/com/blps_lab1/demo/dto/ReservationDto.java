package com.blps_lab1.demo.dto;

import com.blps_lab1.demo.data.tables.*;

import java.time.LocalDate;
import java.util.List;

public class ReservationDto {
    public static Builder builder() {
        return new Builder();
    }

    public ReservationDto(Long id, LocalDate arrival, LocalDate departure, Integer guestsAmount, Integer petsAmount, User user, Place place, double price, PaymentType paymentType, PaymentMethod paymentMethod, Owner owner, List<Long> serviceOptionIds, String coverLetter) {
        this.id = id;
        this.arrival = arrival;
        this.departure = departure;
        this.guestsAmount = guestsAmount;
        this.petsAmount = petsAmount;
        this.user = user;
        this.place = place;
        this.price = price;
        this.paymentType = paymentType;
        this.paymentMethod = paymentMethod;
        this.owner = owner;
        this.serviceOptionIds = serviceOptionIds;
        this.coverLetter = coverLetter;
    }

    private Long id;
    private LocalDate arrival;
    private LocalDate departure;
    private Integer guestsAmount;
    private Integer petsAmount;
    private User user; // Инфа о том кто бронирует
    private Place place;
    private double price;
    private PaymentType paymentType;
    private PaymentMethod paymentMethod;
    private Owner owner;
    private List<Long> serviceOptionIds;
    private String coverLetter;


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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
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

    public static class Builder {
        private Long id;
        private LocalDate arrival;
        private LocalDate departure;
        private Integer guestsAmount;
        private Integer petsAmount;
        private User user;
        private Place place;
        private double price;
        private PaymentType paymentType;
        private PaymentMethod paymentMethod;
        private Owner owner;
        private List<Long> serviceOptionIds;
        private String coverLetter;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder arrival(LocalDate arrival) {
            this.arrival = arrival;
            return this;
        }

        public Builder departure(LocalDate departure) {
            this.departure = departure;
            return this;
        }

        public Builder guestsAmount(Integer guestsAmount) {
            this.guestsAmount = guestsAmount;
            return this;
        }

        public Builder petsAmount(Integer petsAmount) {
            this.petsAmount = petsAmount;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder place(Place place) {
            this.place = place;
            return this;
        }

        public Builder price(double price) {
            this.price = price;
            return this;
        }

        public Builder paymentType(PaymentType paymentType) {
            this.paymentType = paymentType;
            return this;
        }

        public Builder paymentMethod(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public Builder owner(Owner owner) {
            this.owner = owner;
            return this;
        }

        public Builder serviceOptionIds(List<Long> serviceOptionIds) {
            this.serviceOptionIds = serviceOptionIds;
            return this;
        }

        public Builder coverLetter(String coverLetter) {
            this.coverLetter = coverLetter;
            return this;
        }

        public ReservationDto build() {
            return new ReservationDto(
                    id,
                    arrival,
                    departure,
                    guestsAmount,
                    petsAmount,
                    user,
                    place,
                    price,
                    paymentType,
                    paymentMethod,
                    owner,
                    serviceOptionIds,
                    coverLetter
            );
        }
    }
}
