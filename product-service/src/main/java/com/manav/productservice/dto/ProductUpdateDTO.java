package com.manav.productservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ProductUpdateDTO {
    private String name;
    private String brand;
    private String fullDescription;
    private BigDecimal pricing;
    private BigDecimal listPrice;
    private String availabilityStatus;
    private String productCategory;
    private String productDimensions;
    private LocalDate dateFirstAvailable;
    private String manufacturer;
    private String countryOfOrigin;
    private List<String> imageUrls;
    private List<String> featureBullets;
}
