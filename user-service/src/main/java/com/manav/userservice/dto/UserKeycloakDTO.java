package com.manav.userservice.dto;

public record UserKeycloakDTO(
        String username,
        String email,
        String password
) {
    public UserKeycloakDTO{
        if (username == null){
            throw new IllegalArgumentException("Username cannot be null");
        }
        if (email == null){
            throw new IllegalArgumentException("Email cannot be null");
        }
        if (password == null){
            throw new IllegalArgumentException("Password cannot be null");
        }
    }
}