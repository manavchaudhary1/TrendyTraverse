package com.manav.orderservice.mapper;

import com.manav.orderservice.dto.OrderDto;
import com.manav.orderservice.dto.OrderLineDto;
import com.manav.orderservice.model.Order;
import com.manav.orderservice.model.OrderLines;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "orderLines", source = "orderLines")
    OrderDto toOrderDto(Order order, List<OrderLines> orderLines);

    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "price", source = "price")
    OrderLineDto toOrderLineDto(OrderLines orderLine);

    List<OrderLineDto> toOrderLineDtoList(List<OrderLines> orderLines);
}