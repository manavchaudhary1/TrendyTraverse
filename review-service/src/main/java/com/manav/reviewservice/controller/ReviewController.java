package com.manav.reviewservice.controller;


import com.manav.reviewservice.model.Review;
import com.manav.reviewservice.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products/{productId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/{reviewId}")
    public Review getReviewById(@PathVariable Long productId, @PathVariable Long reviewId) {
        return reviewService.findbyId(reviewId);
    }

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.findAll(productId));
    }

    @PostMapping
    public ResponseEntity<Review> createReview(@PathVariable Long productId, @RequestBody Review review) {
        return ResponseEntity.ok(reviewService.createReview(productId, review));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long productId, @PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllReviews(@PathVariable Long productId) {
        reviewService.deleteAllReviews(productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getReviewCount(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewCount(productId));
    }
}
