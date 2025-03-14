package com.manav.productservice.repository;

import com.manav.productservice.model.Review;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRedisRepository extends CrudRepository<Review, Long> {
    List<Review> findByProductId(Long productId);
    void deleteReviewsForProduct(Long productId);
}
