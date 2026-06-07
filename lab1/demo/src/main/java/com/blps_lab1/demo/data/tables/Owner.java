package com.blps_lab1.demo.data.tables;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "Place_owners")
public class Owner {

    public Owner(){}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "login", length = 50, nullable = true, unique = true)
    private String login;

    @Column(name = "name", length = 30, nullable = false, unique = false)
    private String name;

    @Column(name = "requirements_message", nullable = false, unique = false)
    @ColumnDefault("False")
    private Boolean requirenmentsMessage;

    @Column(name = "requirements_photo", nullable = false, unique = false)
    @ColumnDefault("False")
    private Boolean requirementsPhoto;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() { return login; }

    public void setLogin(String login) { this.login = login; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getRequirenmentsMessage() {
        return requirenmentsMessage;
    }

    public void setRequirenmentsMessage(Boolean requirenmentsMessage) {
        this.requirenmentsMessage = requirenmentsMessage;
    }

    public Boolean getRequirenmentsPhoto() {
        return requirementsPhoto;
    }

    public void setRequirenmentsPhoto(Boolean requirenmentsPhoto) {
        this.requirementsPhoto = requirenmentsPhoto;
    }
}
