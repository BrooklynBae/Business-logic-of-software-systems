package data.tables;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@Entity
@Table(name = "Place_reservations")
public class Reservation {
    protected Reservation(){
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "id_place", nullable = false)
    private Place place;

    @ManyToOne
    @JoinColumn(name = "id_owner", nullable = false)
    private User user;
    @Column(name = "arrival", nullable = false)
    private LocalDate arrival;
    @Column(name = "departure", nullable = false)
    private LocalDate departure;

    @Column(name = "guests_amount", nullable = false)
    @Positive
    private Integer guestsAmount;

    @Column(name = "pets_amount", nullable = false)
    @Positive
    private Integer petsAmount;

    @Column(name = "price", nullable = false)
    @Positive
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", length = 25, nullable = false, unique = false)
    private PlaceType placeType;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 25, nullable = false, unique = false)
    private PaymentMethod paymentMethod;

    public PlaceType getPlaceType() {
        return placeType;
    }

    public void setPlaceType(PlaceType placeType) {
        this.placeType = placeType;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Integer getPetsAmount() {
        return petsAmount;
    }

    public void setPetsAmount(Integer petsAmount) {
        this.petsAmount = petsAmount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
