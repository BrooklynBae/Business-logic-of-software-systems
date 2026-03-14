package dto;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "Place_owners")
public class Owner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", length = 30, nullable = false, unique = false)
    private String name;

    @Column(name = "requirenments_message", nullable = false, unique = false)
    @ColumnDefault("False")
    private Boolean requirenmentsMessage;

    @Column(name = "requirenments_photo", nullable = false, unique = false)
    @ColumnDefault("False")
    private Boolean requirenmentsPhoto;
}
