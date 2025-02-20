package com.manav.reviewservice.repository;

import com.manav.reviewservice.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Review findByReviewId(Long reviewId);

    List<Review> findAllByProductId(Long productId);
    int countByProductId(Long productId);
    void deleteByProductId(Long productId);
}

