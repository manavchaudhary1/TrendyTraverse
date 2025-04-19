package com.manav.reviewservice.controller;

import com.manav.reviewservice.model.Review;
import com.manav.reviewservice.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private Review sampleReview;
    private final Long productId = 1L;
    private final Long reviewId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sampleReview = new Review();
        sampleReview.setReviewId(reviewId);
        sampleReview.setProductId(productId);
        sampleReview.setStars(5);
        sampleReview.setReviewDate(LocalDate.now());
        sampleReview.setVerifiedPurchase(true);
        sampleReview.setManufacturerReplied(false);
        sampleReview.setUserId(UUID.randomUUID());
        sampleReview.setTitle("Great product");
        sampleReview.setReviewText("This is an amazing product!");
        sampleReview.setTotalFoundHelpful(10);
        sampleReview.setImages(List.of("image1.jpg", "image2.jpg"));
    }

    @Test
    void getReviewById_ShouldReturnReview() {
        // Arrange
        when(reviewService.findById(reviewId)).thenReturn(sampleReview);

        // Act
        Review result = reviewController.getReviewById(productId, reviewId);

        // Assert
        assertNotNull(result);
        assertEquals(reviewId, result.getReviewId());
        verify(reviewService, times(1)).findById(reviewId);
    }

    @Test
    void getAllReviews_ShouldReturnAllReviews() {
        // Arrange
        List<Review> reviews = new ArrayList<>();
        reviews.add(sampleReview);

        Review review2 = new Review();
        review2.setReviewId(2L);
        review2.setProductId(productId);
        reviews.add(review2);

        when(reviewService.findAll(productId)).thenReturn(reviews);

        // Act
        ResponseEntity<List<Review>> response = reviewController.getAllReviews(productId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<Review> result = response.getBody();
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(reviewService, times(1)).findAll(productId);
    }

    @Test
    void createReview_ShouldCreateAndReturnReview() {
        // Arrange
        Review newReview = new Review();
        newReview.setStars(4);
        newReview.setTitle("Good product");
        newReview.setReviewText("I like this product");

        when(reviewService.createReview(eq(productId), any(Review.class))).thenReturn(sampleReview);

        // Act
        ResponseEntity<Review> response = reviewController.createReview(productId, newReview);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Review result = response.getBody();
        assertNotNull(result);
        assertEquals(reviewId, result.getReviewId());

        verify(reviewService, times(1)).createReview(eq(productId), any(Review.class));
    }

    @Test
    void deleteReview_ShouldDeleteReview() {
        // Arrange
        doNothing().when(reviewService).deleteReview(reviewId);

        // Act
        ResponseEntity<Void> response = reviewController.deleteReview(productId, reviewId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(reviewService, times(1)).deleteReview(reviewId);
    }

    @Test
    void deleteAllReviews_ShouldDeleteAllReviews() {
        // Arrange
        doNothing().when(reviewService).deleteAllReviews(productId);

        // Act
        ResponseEntity<Void> response = reviewController.deleteAllReviews(productId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(reviewService, times(1)).deleteAllReviews(productId);
    }

    @Test
    void getReviewCount_ShouldReturnCount() {
        // Arrange
        when(reviewService.getReviewCount(productId)).thenReturn(5);

        // Act
        ResponseEntity<Integer> response = reviewController.getReviewCount(productId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Integer result = response.getBody();
        assertNotNull(result);
        assertEquals(5, result);

        verify(reviewService, times(1)).getReviewCount(productId);
    }
}