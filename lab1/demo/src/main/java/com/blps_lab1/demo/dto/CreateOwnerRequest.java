package com.blps_lab1.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateOwnerRequest {

    @NotBlank(message = "Login cannot be empty or null")
    private String login;

    @NotBlank(message = "Password cannot be empty or null")
    private String password;

    @NotBlank(message = "Name cannot be empty or null")
    private String name;

    private Boolean requirenmentsMessage;
    private Boolean requirenmentsPhoto;

    public CreateOwnerRequest() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public Boolean getRequirenmentsMessage() {
        return requirenmentsMessage;
    }

    public Boolean getRequirenmentsPhoto() {
        return requirenmentsPhoto;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRequirenmentsMessage(Boolean requirenmentsMessage) {
        this.requirenmentsMessage = requirenmentsMessage;
    }

    public void setRequirenmentsPhoto(Boolean requirenmentsPhoto) {
        this.requirenmentsPhoto = requirenmentsPhoto;
    }
}