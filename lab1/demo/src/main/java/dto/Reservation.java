package dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@Entity
@Table(name = "Place_reservations")
public class Reservation {
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

    @Column(name = "price", nullable = false)
    @Positive
    private double price;
}
