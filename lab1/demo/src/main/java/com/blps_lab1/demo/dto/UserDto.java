package com.blps_lab1.demo.dto;

import jakarta.persistence.Column;

public class UserDto {
    public UserDto(long id, String name, String photo) {
        this.id = id;
        this.name = name;
        this.photo = photo;
    }

    private long id;

    private String name;

    private String photo;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
