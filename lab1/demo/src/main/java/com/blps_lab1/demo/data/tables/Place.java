package com.blps_lab1.demo.data.tables;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "Places")
public class Place {

    public Place(){}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "town", length = 30, nullable = false, unique = false)
    private String town;

    @Column(name = "name", length = 30, nullable = false, unique = false)
    private String name;

    @Column(name = "description", length = 30, nullable = false, unique = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 25, nullable = false, unique = false)
    private PlaceType placeType;

    @Column(name = "price_per_night", nullable = false, unique = false)
    @ColumnDefault("0")
    private double pricePerNight;

    @Column(name = "max_guests", nullable = false, unique = false)
    private int maxGuests;

    @Column(name = "rating", nullable = false, unique = false)
    @ColumnDefault("0")
    private double rating;

    @ManyToOne
    @JoinColumn(name = "id_owner", nullable = false)
    private Owner owner;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PlaceType getPlaceType() {
        return placeType;
    }

    public void setPlaceType(PlaceType placeType) {
        this.placeType = placeType;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public int getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(int maxGuests) {
        this.maxGuests = maxGuests;
    }
}
