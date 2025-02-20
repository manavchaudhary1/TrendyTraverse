package com.manav.orderservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderLineDto {
    private Long productId;
    private int quantity;
    private BigDecimal price;
}
