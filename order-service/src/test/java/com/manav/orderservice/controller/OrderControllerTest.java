package com.manav.orderservice.controller;

import com.manav.orderservice.dto.OrderDto;
import com.manav.orderservice.dto.OrderLineDto;
import com.manav.orderservice.dto.OrderRequestDto;
import com.manav.orderservice.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private UUID userId;
    private UUID orderId;
    private OrderDto mockOrderDto;
    private List<OrderDto> mockOrderDtoList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userId = UUID.randomUUID();
        orderId = UUID.randomUUID();

        // Create mock OrderLineDto
        OrderLineDto lineDto = new OrderLineDto(1L, 2, new BigDecimal("19.99"));
        List<OrderLineDto> orderLines = new ArrayList<>();
        orderLines.add(lineDto);

        // Create mock OrderDto
        Timestamp createdAt = Timestamp.from(Instant.now());
        mockOrderDto = new OrderDto(orderId, userId, createdAt, orderLines);

        // Create mock OrderDto list
        mockOrderDtoList = new ArrayList<>();
        mockOrderDtoList.add(mockOrderDto);
    }

    @Test
    void getUserOrders_ReturnsOrders_WhenOrdersExist() {
        // Arrange
        when(orderService.getAllOrders(userId)).thenReturn(mockOrderDtoList);

        // Act
        ResponseEntity<List<OrderDto>> response = orderController.getUserOrders(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockOrderDtoList, response.getBody());
        verify(orderService, times(1)).getAllOrders(userId);
    }

    @Test
    void getUserOrders_ReturnsEmptyList_WhenNoOrdersExist() {
        // Arrange
        when(orderService.getAllOrders(userId)).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<OrderDto>> response = orderController.getUserOrders(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.emptyList(), response.getBody());
        verify(orderService, times(1)).getAllOrders(userId);
    }

    @Test
    void placeOrderFromCart_ReturnsCreatedOrder_WhenSuccessful() {
        // Arrange
        when(orderService.placeOrderFromCart(userId)).thenReturn(mockOrderDto);

        // Act
        ResponseEntity<OrderDto> response = orderController.placeOrderFromCart(userId);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockOrderDto, response.getBody());
        verify(orderService, times(1)).placeOrderFromCart(userId);
    }

    @Test
    void placeOrderFromCart_ReturnsBadRequest_WhenExceptionThrown() {
        // Arrange
        when(orderService.placeOrderFromCart(userId)).thenThrow(new IllegalStateException("Cart is empty"));

        // Act
        ResponseEntity<OrderDto> response = orderController.placeOrderFromCart(userId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(orderService, times(1)).placeOrderFromCart(userId);
    }

    @Test
    void placeOrder_ReturnsCreatedOrder_WhenSuccessful() {
        // Arrange
        OrderRequestDto requestDto = new OrderRequestDto(1L, 2);

        when(orderService.placeOrder(userId, requestDto.productId(), requestDto.quantity()))
                .thenReturn(mockOrderDto);

        // Act
        ResponseEntity<OrderDto> response = orderController.placeOrder(userId, requestDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockOrderDto, response.getBody());
        verify(orderService, times(1))
                .placeOrder(userId, requestDto.productId(), requestDto.quantity());
    }

    @Test
    void placeOrder_ReturnsBadRequest_WhenQuantityIsZero() {
        // Arrange
        OrderRequestDto requestDto = new OrderRequestDto(1L, 0);

        // Act
        ResponseEntity<OrderDto> response = orderController.placeOrder(userId, requestDto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(orderService, never()).placeOrder(any(), any(), anyInt());
    }

    @Test
    void placeOrder_ReturnsBadRequest_WhenExceptionThrown() {
        // Arrange
        OrderRequestDto requestDto = new OrderRequestDto(1L, 2);

        when(orderService.placeOrder(userId, requestDto.productId(), requestDto.quantity()))
                .thenThrow(new IllegalStateException("Product not found"));

        // Act
        ResponseEntity<OrderDto> response = orderController.placeOrder(userId, requestDto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(orderService, times(1))
                .placeOrder(userId, requestDto.productId(), requestDto.quantity());
    }

    @Test
    void deleteOrder_ReturnsNoContent_WhenSuccessful() {
        // Arrange
        doNothing().when(orderService).deleteOrder(orderId, userId);

        // Act
        ResponseEntity<Void> response = orderController.deleteOrder(userId, orderId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderService, times(1)).deleteOrder(orderId, userId);
    }

    @Test
    void deleteOrder_ReturnsNotFound_WhenOrderNotFound() {
        // Arrange
        doThrow(new IllegalStateException("Order not found")).when(orderService).deleteOrder(orderId, userId);

        // Act
        ResponseEntity<Void> response = orderController.deleteOrder(userId, orderId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(orderService, times(1)).deleteOrder(orderId, userId);
    }

    @Test
    void deleteOrder_ReturnsForbidden_WhenUnauthorized() {
        // Arrange
        doThrow(new IllegalStateException("Unauthorized access")).when(orderService).deleteOrder(orderId, userId);

        // Act
        ResponseEntity<Void> response = orderController.deleteOrder(userId, orderId);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(orderService, times(1)).deleteOrder(orderId, userId);
    }
}