package com.manav.productservice.dto;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ReviewDTO {
    @Id
    private Long reviewId;
    private Long productId;
    private Integer stars;
    private LocalDate reviewDate;
    private Boolean verifiedPurchase;
    private Boolean manufacturerReplied;
    private UUID userId;
    private String title;
    private String reviewText;
    private Integer totalFoundHelpful;
    private List<String> images;
}
