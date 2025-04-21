package com.manav.productservice.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ReviewDTO(
        Long reviewId,
        Long productId,
        Integer stars,
        LocalDate reviewDate,
        Boolean verifiedPurchase,
        Boolean manufacturerReplied,
        UUID userId,
        String title,
        String reviewText,
        Integer totalFoundHelpful,
        List<String> images
) {}