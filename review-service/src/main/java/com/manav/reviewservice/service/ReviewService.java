package com.manav.reviewservice.service;

import com.manav.reviewservice.events.source.ReviewSourceBean;
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
    private final ReviewSourceBean reviewSourceBean;

    public ReviewService(ReviewRepository reviewRepository, ReviewSourceBean reviewSourceBean) {
        this.reviewRepository = reviewRepository;
        this.reviewSourceBean = reviewSourceBean;
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
        Review savedReview = reviewRepository.save(review);

        reviewSourceBean.publishReviewChange("SAVE", savedReview.getProductId());
        return savedReview;
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        Long productId = review.getProductId();
        reviewRepository.deleteById(reviewId);
        reviewSourceBean.publishReviewChange("DELETE", productId);
    }

    public Integer getReviewCount(Long productId) {
        return reviewRepository.countByProductId(productId);
    }

    @Transactional
    public void deleteAllReviews(Long productId) {
        reviewRepository.deleteByProductId(productId);
        reviewSourceBean.publishReviewChange("DELETE", productId);
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
