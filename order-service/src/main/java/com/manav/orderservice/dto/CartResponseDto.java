package com.manav.orderservice.dto;

import com.manav.orderservice.model.CartItem;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public record CartResponseDto(
        UUID cartId,
        UUID userId,
        Timestamp createdAt,
        Timestamp updatedAt,
        boolean archived,
        List<CartItem> items
) {}