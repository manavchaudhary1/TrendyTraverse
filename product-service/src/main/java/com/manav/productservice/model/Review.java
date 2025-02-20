package com.manav.productservice.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class Review {

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
