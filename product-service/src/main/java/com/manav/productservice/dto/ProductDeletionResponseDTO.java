package com.manav.productservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ProductDeletionResponseDTO {
    private Long productId;
    private String productName;
    private int imagesDeleted;
    private int featuresDeleted;
    private LocalDateTime deletionTimestamp;
    private String message;
    private int reviewsDeleted;
}
