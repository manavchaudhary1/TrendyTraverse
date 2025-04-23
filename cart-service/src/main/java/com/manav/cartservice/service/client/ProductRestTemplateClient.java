package com.manav.cartservice.service.client;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductRestTemplateClient {

    private final RestTemplate restTemplate;

    public BigDecimal getPricing(Long productId) {
        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                "http://gateway-server:8072/product-service/products/{productId}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {},
                productId
        );

        Map<String, Object> responseBody = responseEntity.getBody();
        if (responseBody != null && responseBody.containsKey("pricing")) {
            return new BigDecimal(responseBody.get("pricing").toString());
        }
        return BigDecimal.ZERO;
    }
}