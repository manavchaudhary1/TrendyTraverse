package com.manav.productservice.dto;

import com.manav.productservice.model.Review;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ProductResponseDTO {
    private Long productId;
    private String name;
    private String brand;
    private List<ProductImageDTO> productImages;
    private String fullDescription;
    private List<ProductFeatureDTO> featureBullets;
    private BigDecimal pricing;
    private BigDecimal listPrice;
    private String availabilityStatus;
    private String productCategory;
    private String productDimensions;
    private LocalDate dateFirstAvailable;
    private String manufacturer;
    private String countryOfOrigin;
    private Double averageRating;
    private Integer totalReviews;
    private Integer fiveStarReviews;
    private Integer fourStarReviews;
    private Integer threeStarReviews;
    private Integer twoStarReviews;
    private Integer oneStarReviews;
    private List<ReviewDTO> reviews;
}