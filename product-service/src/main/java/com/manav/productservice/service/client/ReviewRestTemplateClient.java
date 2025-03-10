package com.manav.productservice.service.client;

import com.manav.productservice.model.Review;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Slf4j
public class ReviewRestTemplateClient {

    private final RestTemplate restTemplate;

    public ReviewRestTemplateClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Review> getAllReviews(Long productId) {
        log.info("Fetching reviews of product with id: {}", productId);
        ResponseEntity<List<Review>> responseEntity = restTemplate.exchange(
                "http://localhost:8091/products/{productId}/reviews",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {},
                productId
        );
        return responseEntity.getBody();
    }

    public void deleteAllReviews(Long productId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAccessToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Void> responseEntity = restTemplate.exchange(
                "http://localhost:8091/products/{productId}/reviews",
                HttpMethod.DELETE,
                entity,
                Void.class,
                productId
        );
        responseEntity.getBody();
    }

    public Integer getReviewCount(Long productId) {
        log.info("Fetching review count of product with id: {}", productId);
        ResponseEntity<Integer> responseEntity = restTemplate.exchange(
                "http://localhost:8091/products/{productId}/reviews/count",
                HttpMethod.GET,
                null,
                Integer.class,
                productId
        );
        return responseEntity.getBody();
    }

    private String getAccessToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof Jwt jwt) {
                return jwt.getTokenValue();
            } else {
                log.error("Expected Jwt object but found: {}", principal);
            }
        } else {
            log.error("Authentication object is null");
        }
        throw new RuntimeException("No valid token found");
    }
}