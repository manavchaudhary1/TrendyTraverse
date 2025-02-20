package com.manav.productservice.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ProductCreateDTO {
    @NonNull
    private String name;
    @NonNull
    private String brand;
    private String fullDescription;
    @NonNull
    private BigDecimal pricing;
    private BigDecimal listPrice;
    @NonNull
    private String availabilityStatus;
    private String productCategory;
    private String productDimensions;
    private LocalDate dateFirstAvailable;
    private String manufacturer;
    private String countryOfOrigin;
    private List<String> imageUrls; // List of image URLs to be created
    private List<String> featureBullets; // List of feature descriptions
}