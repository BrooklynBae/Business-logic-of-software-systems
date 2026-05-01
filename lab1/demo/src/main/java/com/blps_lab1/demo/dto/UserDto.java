package com.blps_lab1.demo.dto;

public class UserDto {
    public static Builder builder() {
        return new Builder();
    }

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

    public static class Builder {
        private long id;
        private String name;
        private String photo;

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder photo(String photo) {
            this.photo = photo;
            return this;
        }

        public UserDto build() {
            return new UserDto(id, name, photo);
        }
    }
}
