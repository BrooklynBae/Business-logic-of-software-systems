package com.blps_lab1.demo.dto;

public class CreateUserRequest {
    private String name;
    private String photo;

    public CreateUserRequest() {
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}