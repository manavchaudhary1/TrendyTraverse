package com.manav.productservice.service.client;

import com.manav.productservice.model.Review;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
        ResponseEntity<Void> responseEntity = restTemplate.exchange(
                "http://localhost:8091/products/{productId}/reviews",
                HttpMethod.DELETE,
                null,
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
}