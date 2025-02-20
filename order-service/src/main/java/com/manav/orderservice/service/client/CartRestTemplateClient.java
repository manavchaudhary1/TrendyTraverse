package com.manav.orderservice.service.client;

import com.manav.orderservice.dto.CartResponseDto;
import com.manav.orderservice.model.CartItem;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class CartRestTemplateClient {

    private final RestTemplate restTemplate;

    public CartRestTemplateClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<CartItem> getCartItems(UUID userId) {
        ResponseEntity<CartResponseDto> responseEntity = restTemplate.getForEntity(
                "http://localhost:8092/cart/{userId}",
                CartResponseDto.class,
                userId
        );

        CartResponseDto cartResponseDto = responseEntity.getBody();
        if (cartResponseDto == null) {
            return Collections.emptyList();
        }
        return cartResponseDto.getItems();
    }

    public void archiveCart(UUID userId) {
        restTemplate.exchange(
                "http://localhost:8092/cart/{userId}/checkout",
                HttpMethod.POST,
                null,
                Void.class,
                userId);
    }
}
