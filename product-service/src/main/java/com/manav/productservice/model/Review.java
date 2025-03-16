package com.manav.productservice.model;


import org.springframework.data.annotation.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@RedisHash("Review")
public class Review {

    @Id
    private Long reviewId;
    @Indexed
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
