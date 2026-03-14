package dto;

import jakarta.persistence.*;

@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", length = 30, nullable = false, unique = false)
    private String name;

    @Column(name = "photo", nullable = true, unique = false)
    private String photo;
}
