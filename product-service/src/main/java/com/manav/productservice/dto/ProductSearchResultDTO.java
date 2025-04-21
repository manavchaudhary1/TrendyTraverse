package com.manav.productservice.dto;

import java.math.BigDecimal;

public record ProductSearchResultDTO(
        Long productId,
        String name,
        BigDecimal pricing,
        String firstImage
) {}