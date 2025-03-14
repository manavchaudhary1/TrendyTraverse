package com.manav.reviewservice.service;

import com.manav.reviewservice.model.Review;
import com.manav.reviewservice.repository.ReviewRepository;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review findById(Long reviewId) {
        return reviewRepository.findById(reviewId).orElse(null);
    }

    @CircuitBreaker(name = "reviewService", fallbackMethod = "buildFallBackReview")
    @RateLimiter(name = "reviewService", fallbackMethod = "buildFallBackReview")
    @Retry(name = "retryReviewService", fallbackMethod = "buildFallBackReview")
    @Bulkhead(name = "bulkheadReviewService",type = Bulkhead.Type.SEMAPHORE, fallbackMethod = "buildFallBackReview")
    public List<Review> findAll(Long productId) {
        return reviewRepository.findAllByProductId(productId);
    }


    public Review createReview(Long productId, Review review) {
        if (review == null) {
            throw new IllegalArgumentException("Review cannot be null");
        }
        review.setProductId(productId);
        review.setReviewDate(LocalDate.now());
        return reviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    public Integer getReviewCount(Long productId) {
        return reviewRepository.countByProductId(productId);
    }

    @Transactional
    public void deleteAllReviews(Long productId) {
        reviewRepository.deleteByProductId(productId);
    }

    @SuppressWarnings("unused")
    private List<Review> buildFallBackReview(Long productId, Throwable throwable) {
        Review review = new Review();
        review.setReviewId(0L);
        review.setProductId(0L);
        review.setStars(0);
        review.setReviewDate(null);
        review.setVerifiedPurchase(false);
        review.setManufacturerReplied(false);
        review.setUserId(null);
        review.setTitle("Fallback Review");
        review.setReviewText("Fallback Review");
        review.setTotalFoundHelpful(0);
        review.setImages(List.of());
        return List.of(review);
    }
}
