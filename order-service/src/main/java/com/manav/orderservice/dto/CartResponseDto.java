package com.manav.orderservice.dto;

import com.manav.orderservice.model.CartItem;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Data
public class CartResponseDto {
    private UUID cartId;
    private UUID userId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean archived;
    private List<CartItem> items;
}