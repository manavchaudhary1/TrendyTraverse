package com.manav.orderservice.service.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class ProductRestTemplateClient {

    private final RestTemplate restTemplate;

    public ProductRestTemplateClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BigDecimal getPricing(Long productId) {
        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                "http://localhost:8090/products/{productId}",
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