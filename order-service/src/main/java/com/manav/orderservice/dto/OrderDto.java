package com.manav.orderservice.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Data
public class OrderDto {
    private UUID id;
    private UUID userId;
    private Timestamp createdAt;
    private List<OrderLineDto> orderLines;
}
