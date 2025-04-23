package com.manav.cartservice.dto;

public record AddCartItemRequest(
        Long productId,
        int quantity
) {
    public AddCartItemRequest {
        if (productId == null) {
            throw new IllegalArgumentException("productId cannot be null");
        }
    }
}