package com.manav.reviewservice.service;

import com.manav.reviewservice.model.Review;
import com.manav.reviewservice.repository.ReviewRepository;
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

    public Review findbyId(Long reviewId) {
        return reviewRepository.findById(reviewId).orElse(null);
    }

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
}
