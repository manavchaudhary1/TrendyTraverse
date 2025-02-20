package com.manav.cartservice.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class UpdateCartItemRequest {
    @NonNull
    private Long productId;
    private int quantity;
}

