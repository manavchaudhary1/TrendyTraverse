package com.manav.orderservice.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItem {

    private Long productId;
    private int quantity;
    private BigDecimal price;
}
