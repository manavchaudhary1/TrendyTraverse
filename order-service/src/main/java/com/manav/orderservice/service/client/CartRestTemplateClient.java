package com.manav.orderservice.service.client;

import com.manav.orderservice.dto.CartResponseDto;
import com.manav.orderservice.model.CartItem;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
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
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAccessToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<CartResponseDto> responseEntity = restTemplate.exchange(
                "http://localhost:8092/cart/{userId}",
                HttpMethod.GET,
                entity,
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
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAccessToken());

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        restTemplate.exchange(
                "http://localhost:8092/cart/{userId}/checkout",
                HttpMethod.POST,
                entity,
                Void.class,
                userId
        );
    }

    private String getAccessToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getCredentials() instanceof String token) {
            return token;
        }
        throw new RuntimeException("No valid token found");
    }
}
