package com.manav.productservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ProductResponseDTO(
        Long productId,
        String name,
        String brand,
        List<ProductImageDTO> productImages,
        String fullDescription,
        List<ProductFeatureDTO> featureBullets,
        BigDecimal pricing,
        BigDecimal listPrice,
        String availabilityStatus,
        String productCategory,
        String productDimensions,
        LocalDate dateFirstAvailable,
        String manufacturer,
        String countryOfOrigin,
        Double averageRating,
        Integer totalReviews,
        Integer fiveStarReviews,
        Integer fourStarReviews,
        Integer threeStarReviews,
        Integer twoStarReviews,
        Integer oneStarReviews,
        List<ReviewDTO> reviews
) {}