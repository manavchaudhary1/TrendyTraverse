package com.manav.productservice.dto;

import java.time.LocalDateTime;

public record ProductDeletionResponseDTO(
        Long productId,
        String productName,
        int imagesDeleted,
        int featuresDeleted,
        int reviewsDeleted,
        LocalDateTime deletionTimestamp,
        String message
) {}