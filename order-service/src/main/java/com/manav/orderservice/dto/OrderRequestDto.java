package com.manav.orderservice.dto;

public record OrderRequestDto(
        Long productId,
        int quantity
) {}