package com.univbuc.bookreview.dto;

public class UserRegistrationDto {

    private String username;
    private String password;

    // Default constructor
    public UserRegistrationDto() {
    }

    // Parameterized constructor for easy object creation
    public UserRegistrationDto(String username, String email, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
