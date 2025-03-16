package com.manav.reviewservice.events.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewChangeModel {
    private String type;
    private String action;
    private Long productId;
    private String correlationId;
}