package com.blps_lab1.demo.dto;

public class OwnerDto {
    public static Builder builder() {
        return new Builder();
    }

    private Long id;
    private String name;
    private Boolean requirenmentsMessage;
    private Boolean requirenmentsPhoto;

    public OwnerDto() {
    }

    public OwnerDto(Long id, String name, Boolean requirenmentsMessage, Boolean requirenmentsPhoto) {
        this.id = id;
        this.name = name;
        this.requirenmentsMessage = requirenmentsMessage;
        this.requirenmentsPhoto = requirenmentsPhoto;
    }

    public Long getId() {
        return id;
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

    public void setId(Long id) {
        this.id = id;
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

    public static class Builder {
        private Long id;
        private String name;
        private Boolean requirenmentsMessage;
        private Boolean requirenmentsPhoto;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder requirenmentsMessage(Boolean requirenmentsMessage) {
            this.requirenmentsMessage = requirenmentsMessage;
            return this;
        }

        public Builder requirenmentsPhoto(Boolean requirenmentsPhoto) {
            this.requirenmentsPhoto = requirenmentsPhoto;
            return this;
        }

        public OwnerDto build() {
            return new OwnerDto(id, name, requirenmentsMessage, requirenmentsPhoto);
        }
    }
}