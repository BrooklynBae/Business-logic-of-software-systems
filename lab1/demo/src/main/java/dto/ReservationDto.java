package dto;

import data.tables.Owner;
import data.tables.User;
import jakarta.persistence.Column;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

public class ReservationDto {
    public ReservationDto(Long id, LocalDate arrival, LocalDate departure, Integer guestsAmount, Integer petsAmount, User user, double price, String paymentType, String paymentMethod, Owner owner) {
        this.id = id;
        this.arrival = arrival;
        this.departure = departure;
        this.guestsAmount = guestsAmount;
        this.petsAmount = petsAmount;
        this.user = user;
        this.price = price;
        this.paymentType = paymentType;
        this.paymentMethod = paymentMethod;
        this.owner = owner;
    }

    private Long id;
    private LocalDate arrival;
    private LocalDate departure;
    private Integer guestsAmount;
    private Integer petsAmount;
    private User user;
    private double price;
    private String paymentType;
    private String paymentMethod;
    private Owner owner;

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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }
}
