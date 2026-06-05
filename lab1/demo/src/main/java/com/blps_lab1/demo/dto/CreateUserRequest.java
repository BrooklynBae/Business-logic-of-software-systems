package com.blps_lab1.demo.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class CreateUserRequest {

    @NotBlank(message = "Login cannot be empty or null")
    private String login;

    @NotBlank(message = "Password cannot be empty or null")
    private String password;

    @NotBlank(message = "Name cannot be empty or null")
    private String name;

    private String photo;

    private List<String> roles;
    private List<String> authorities;

    public CreateUserRequest() {
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

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }
}