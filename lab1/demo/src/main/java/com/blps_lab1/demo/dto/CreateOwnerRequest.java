package com.blps_lab1.demo.dto;

public class CreateOwnerRequest {
    private String name;
    private Boolean requirenmentsMessage;
    private Boolean requirenmentsPhoto;

    public CreateOwnerRequest() {
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