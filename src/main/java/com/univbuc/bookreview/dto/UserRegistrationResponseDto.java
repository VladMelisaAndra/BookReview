package com.univbuc.bookreview.dto;

public class UserRegistrationResponseDto {

    private String username;
    private String email;

    // Default constructor
    public UserRegistrationResponseDto() {
    }

    // Parameterized constructor for easy object creation
    public UserRegistrationResponseDto(String username, String email) {
        this.username = username;
        this.email = email;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
