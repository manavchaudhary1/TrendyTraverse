package com.manav.productservice.service.client;

import com.manav.productservice.exception.CustomException;
import com.manav.productservice.model.Review;
import com.manav.productservice.repository.ReviewRedisRepository;
import java.util.Collections;
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
    private final ReviewRedisRepository reviewRedisRepository;

    public ReviewRestTemplateClient(RestTemplate restTemplate, ReviewRedisRepository reviewRedisRepository) {
        this.restTemplate = restTemplate;
        this.reviewRedisRepository = reviewRedisRepository;
    }

    public List<Review> getAllReviews(Long productId) {
        try {
            // First check if reviews are in the Redis cache
            List<Review> cachedReviews = checkRedisCache(productId);
            if (cachedReviews != null && !cachedReviews.isEmpty()) {
                return cachedReviews;
            } else {
                // If not in cache, fetch from the review service
                ResponseEntity<List<Review>> responseEntity = restTemplate.exchange(
                        "http://localhost:8091/products/{productId}/reviews",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {},
                        productId
                );

                List<Review> reviews = responseEntity.getBody();

                // Cache the newly fetched reviews
                if (reviews != null && !reviews.isEmpty()) {
                    cacheReviewsList(productId, reviews);
                }

                return reviews;
            }
        } catch (Exception e) {
            log.error("Error fetching reviews for product {}: {}", productId, e.getMessage());
            throw new CustomException("Error Fetching Reviews from Review Service");
        }
    }


    public void deleteAllReviews(Long productId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(getAccessToken());
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            restTemplate.exchange(
                    "http://localhost:8091/products/{productId}/reviews",
                    HttpMethod.DELETE,
                    entity,
                    Void.class,
                    productId
            );
            // Delete from cache as well
            deleteCacheReviewsList(productId);
        } catch (Exception e) {
            log.error("Error deleting reviews for product {}: {}", productId, e.getMessage(), e);
        }
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
        throw new CustomException("No valid token found");
    }

    private List<Review> checkRedisCache(Long productId) {
        try {
            return reviewRedisRepository.findByProductId(productId);
        } catch (Exception exception) {
            log.warn("Error retrieving reviews for product {} from Redis cache: {}",
                    productId, exception.getMessage());
            return Collections.emptyList();
        }
    }
    private void cacheReviewsList(Long productId, List<Review> reviews) {
        try {
            log.info("Saving reviews of product with id: {}", productId);
            reviewRedisRepository.saveAll(reviews);
        } catch (Exception exception) {
            log.warn("Error caching reviews for product {} in Redis: {}",
                    productId, exception.getMessage());
        }
    }

    private void deleteCacheReviewsList(Long productId) {
        try {
            reviewRedisRepository.deleteReviewsForProduct(productId);
        } catch (Exception exception) {
            log.warn("Error deleting reviews for product {} in Redis: {}",
                    productId, exception.getMessage());
        }
    }
}