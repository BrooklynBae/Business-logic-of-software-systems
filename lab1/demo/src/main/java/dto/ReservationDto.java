package dto;

import data.tables.Owner;
import data.tables.PaymentMethod;
import data.tables.PaymentType;
import data.tables.User;
import jakarta.persistence.Column;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

public class ReservationDto {
    public ReservationDto(Long id, LocalDate arrival, LocalDate departure, Integer guestsAmount, Integer petsAmount, User user, double price, PaymentType paymentType, PaymentMethod paymentMethod, Owner owner) {
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
    private User user; // Инфа о том кто бронирует
    private double price;
    private PaymentType paymentType;
    private PaymentMethod paymentMethod;
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
}
