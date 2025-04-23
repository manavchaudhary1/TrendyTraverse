package com.manav.orderservice.dto;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public record OrderDto(
        UUID id,
        UUID userId,
        Timestamp createdAt,
        List<OrderLineDto> orderLines
) {}