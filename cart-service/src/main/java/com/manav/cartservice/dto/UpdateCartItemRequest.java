package com.manav.cartservice.dto;

public record UpdateCartItemRequest(
        Long productId,
        int quantity
) {
    public UpdateCartItemRequest {
        if (productId == null) {
            throw new IllegalArgumentException("productId cannot be null");
        }
    }
}