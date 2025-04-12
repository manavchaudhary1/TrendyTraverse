package com.manav.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchResultDTO {
    private Long productId;
    private String name;
    private BigDecimal pricing;
    private String firstImage;
}