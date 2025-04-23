package com.manav.orderservice.dto;

import java.math.BigDecimal;

public record OrderLineDto(
        Long productId,
        int quantity,
        BigDecimal price
) {}