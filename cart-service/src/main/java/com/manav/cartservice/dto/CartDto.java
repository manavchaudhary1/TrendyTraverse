package com.manav.cartservice.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Data
public class CartDto {
    private UUID cartId;
    private UUID userId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean archived;
    private List<CartItemDto> items;
}