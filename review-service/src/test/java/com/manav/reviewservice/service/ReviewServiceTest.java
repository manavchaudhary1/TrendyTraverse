package com.manav.reviewservice.service;

import com.manav.reviewservice.events.source.ReviewSourceBean;
import com.manav.reviewservice.model.Review;
import com.manav.reviewservice.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewSourceBean reviewSourceBean;

    @InjectMocks
    private ReviewService reviewService;

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
    void findById_ShouldReturnReview_WhenReviewExists() {
        // Arrange
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(sampleReview));

        // Act
        Review result = reviewService.findById(reviewId);

        // Assert
        assertNotNull(result);
        assertEquals(reviewId, result.getReviewId());
        verify(reviewRepository, times(1)).findById(reviewId);
    }

    @Test
    void findById_ShouldReturnNull_WhenReviewDoesNotExist() {
        // Arrange
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Review result = reviewService.findById(999L);

        // Assert
        assertNull(result);
        verify(reviewRepository, times(1)).findById(999L);
    }

    @Test
    void findAll_ShouldReturnAllReviews_ForGivenProductId() {
        // Arrange
        List<Review> reviews = new ArrayList<>();
        reviews.add(sampleReview);

        Review review2 = new Review();
        review2.setReviewId(2L);
        review2.setProductId(productId);
        reviews.add(review2);

        when(reviewRepository.findAllByProductId(productId)).thenReturn(reviews);

        // Act
        List<Review> result = reviewService.findAll(productId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(reviewRepository, times(1)).findAllByProductId(productId);
    }

    @Test
    void createReview_ShouldSaveAndReturnReview() {
        // Arrange
        Review newReview = new Review();
        newReview.setStars(4);
        newReview.setTitle("Good product");
        newReview.setReviewText("I like this product");

        when(reviewRepository.save(any(Review.class))).thenReturn(sampleReview);

        // Act
        Review result = reviewService.createReview(productId, newReview);

        // Assert
        assertNotNull(result);
        assertEquals(reviewId, result.getReviewId());
        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(reviewSourceBean, times(1)).publishReviewChange(eq("SAVE"), eq(productId));
    }

    @Test
    void createReview_ShouldThrowException_WhenReviewIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> reviewService.createReview(productId, null));

        verify(reviewRepository, never()).save(any(Review.class));
        verify(reviewSourceBean, never()).publishReviewChange(anyString(), anyLong());
    }

    @Test
    void deleteReview_ShouldDeleteReview_WhenReviewExists() {
        // Arrange
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(sampleReview));

        // Act
        reviewService.deleteReview(reviewId);

        // Assert
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).deleteById(reviewId);
        verify(reviewSourceBean, times(1)).publishReviewChange(eq("DELETE"), eq(productId));
    }

    @Test
    void deleteReview_ShouldThrowException_WhenReviewDoesNotExist() {
        // Arrange
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> reviewService.deleteReview(999L));

        verify(reviewRepository, times(1)).findById(999L);
        verify(reviewRepository, never()).deleteById(anyLong());
        verify(reviewSourceBean, never()).publishReviewChange(anyString(), anyLong());
    }

    @Test
    void getReviewCount_ShouldReturnCount() {
        // Arrange
        when(reviewRepository.countByProductId(productId)).thenReturn(5);

        // Act
        Integer result = reviewService.getReviewCount(productId);

        // Assert
        assertEquals(5, result);
        verify(reviewRepository, times(1)).countByProductId(productId);
    }

    @Test
    void deleteAllReviews_ShouldDeleteAllReviews_ForGivenProductId() {
        // Act
        reviewService.deleteAllReviews(productId);

        // Assert
        verify(reviewRepository, times(1)).deleteByProductId(productId);
        verify(reviewSourceBean, times(1)).publishReviewChange(eq("DELETE"), eq(productId));
    }

    @Test
    void buildFallBackReview_ShouldReturnFallbackReview() throws Exception {
        // We need to use reflection to test private method
        java.lang.reflect.Method buildFallBackReviewMethod = ReviewService.class.getDeclaredMethod(
                "buildFallBackReview", Long.class, Throwable.class);
        buildFallBackReviewMethod.setAccessible(true);

        // Act
        @SuppressWarnings("unchecked")
        List<Review> result = (List<Review>) buildFallBackReviewMethod.invoke(
                reviewService, productId, new RuntimeException("Test exception"));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        Review fallbackReview = result.getFirst();
        assertEquals(0L, fallbackReview.getReviewId());
        assertEquals("Fallback Review", fallbackReview.getTitle());
    }
}
