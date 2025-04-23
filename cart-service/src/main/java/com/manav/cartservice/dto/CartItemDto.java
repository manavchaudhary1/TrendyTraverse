package com.manav.cartservice.dto;

import java.math.BigDecimal;

public record CartItemDto(
        Long productId,
        int quantity,
        BigDecimal price
) {}