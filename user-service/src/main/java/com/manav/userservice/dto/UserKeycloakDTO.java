package com.manav.userservice.dto;

public record UserKeycloakDTO(
        String username,
        String email,
        String password
) {
    public UserKeycloakDTO{
        if (username == null){
            throw new IllegalArgumentException("Entry cannot be null");
        }
        if (password == null){
            throw new IllegalArgumentException("Password cannot be null");
        }
    }
}