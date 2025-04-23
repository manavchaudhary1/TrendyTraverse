package com.manav.userservice.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
        UUID id,
        String username,
        String email,
        LocalDateTime createdAt,
        LocalDateTime lastLogin
) {
}