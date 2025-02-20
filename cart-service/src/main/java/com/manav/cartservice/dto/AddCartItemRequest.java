package com.manav.cartservice.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class AddCartItemRequest {
    @NonNull
    private Long productId;
    private int quantity;
}
