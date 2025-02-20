package com.manav.reviewservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "reviews", schema = "public")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "stars")
    private Integer stars;

    @Column(name = "review_date")
    private LocalDate reviewDate;

    @Column(name = "verified_purchase")
    private Boolean verifiedPurchase;

    @Column(name = "manufacturer_replied")
    private Boolean manufacturerReplied;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "title")
    private String title;

    @Column(name = "review", columnDefinition = "TEXT")
    private String reviewText;

    @Column(name = "total_found_helpful")
    private Integer totalFoundHelpful;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "images",columnDefinition = "jsonb")
    private List<String> images;
}
