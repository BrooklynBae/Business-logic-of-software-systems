package dto;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "Places")
public class Place {

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

    @Column(name = "rating", nullable = false, unique = false)
    @ColumnDefault("0")
    private double rating;

    @ManyToOne
    @JoinColumn(name = "id_owner", nullable = false)
    private Owner owner;
}
