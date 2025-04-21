package com.manav.productservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ProductCreateDTO(
        String name,
        String brand,
        String fullDescription,
        BigDecimal pricing,
        BigDecimal listPrice,
        String availabilityStatus,
        String productCategory,
        String productDimensions,
        LocalDate dateFirstAvailable,
        String manufacturer,
        String countryOfOrigin,
        List<String> imageUrls,
        List<String> featureBullets
) {}