package com.manav.orderservice.controller;

import com.manav.orderservice.dto.OrderDto;
import com.manav.orderservice.dto.OrderRequestDto;
import com.manav.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders/{userId}")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getUserOrders(@PathVariable UUID userId) {
        List<OrderDto> orders = orderService.getAllOrders(userId);
        return orders.isEmpty()
                ? ResponseEntity.ok(Collections.emptyList())
                : ResponseEntity.ok(orders);
    }

    @PostMapping("/cart")
    public ResponseEntity<OrderDto> placeOrderFromCart(@PathVariable UUID userId) {
        try {
            OrderDto order = orderService.placeOrderFromCart(userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<OrderDto> placeOrder(
            @PathVariable UUID userId,
            @RequestBody OrderRequestDto orderRequestDto) {
        try {
            if (orderRequestDto.getQuantity() <= 0) {
                return ResponseEntity.badRequest().build();
            }

            OrderDto order = orderService.placeOrder(userId, orderRequestDto.getProductId(), orderRequestDto.getQuantity());
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable UUID userId,
            @PathVariable UUID orderId) {
        try {
            orderService.deleteOrder(orderId, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(
                    e.getMessage().contains("not found")
                            ? HttpStatus.NOT_FOUND
                            : HttpStatus.FORBIDDEN
            ).build();
        }
    }
}
