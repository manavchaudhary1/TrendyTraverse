package com.manav.productservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products", schema = "public")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "brand")
    private String brand;

    @Column(name = "full_description", columnDefinition = "TEXT")
    private String fullDescription;

    @Column(name = "pricing")
    private BigDecimal pricing;

    @Column(name = "list_price")
    private BigDecimal listPrice;

    @Column(name = "availability_status")
    private String availabilityStatus;

    @Column(name = "product_category")
    private String productCategory;

    @Column(name = "product_dimensions")
    private String productDimensions;

    @Column(name = "date_first_available")
    private LocalDate dateFirstAvailable;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "country_of_origin")
    private String countryOfOrigin;

    @Column(name = "average_rating")
    private Double averageRating;

    @Column(name = "total_reviews")
    private Integer totalReviews;

    @Column(name = "five_star_reviews")
    private Integer fiveStarReviews;

    @Column(name = "four_star_reviews")
    private Integer fourStarReviews;

    @Column(name = "three_star_reviews")
    private Integer threeStarReviews;

    @Column(name = "two_star_reviews")
    private Integer twoStarReviews;

    @Column(name = "one_star_reviews")
    private Integer oneStarReviews;

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<ProductImage> productImages;

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<ProductFeatures> featureBullets;
}


