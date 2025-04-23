package com.manav.cartservice.dto;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public record CartDto(
        UUID cartId,
        UUID userId,
        Timestamp createdAt,
        Timestamp updatedAt,
        boolean archived,
        List<CartItemDto> items
) {}