package com.manav.orderservice.service;

import com.manav.orderservice.dto.OrderDto;
import com.manav.orderservice.exception.CustomException;
import com.manav.orderservice.exception.UnauthorizedAccessException;
import com.manav.orderservice.mapper.OrderMapper;
import com.manav.orderservice.model.CartItem;
import com.manav.orderservice.model.Order;
import com.manav.orderservice.model.OrderLines;
import com.manav.orderservice.repository.OrderLineRepository;
import com.manav.orderservice.repository.OrderRepository;
import com.manav.orderservice.service.client.CartRestTemplateClient;
import com.manav.orderservice.service.client.ProductRestTemplateClient;
import com.manav.orderservice.service.client.UserRestTemplateClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderLineRepository orderLineRepository;

    @Mock
    private CartRestTemplateClient cartRestTemplateClient;

    @Mock
    private ProductRestTemplateClient productRestTemplateClient;

    @Mock
    private UserRestTemplateClient userRestTemplateClient;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Spy
    @InjectMocks
    private OrderService orderService;

    private UUID userId;
    private UUID orderId;
    private Order mockOrder;
    private OrderLines mockOrderLine;
    private List<CartItem> mockCartItems;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Mock getUsernameFromJwt to bypass JWT authentication
        doReturn("test-user").when(orderService).getUsernameFromJwt();

        userId = UUID.randomUUID();
        orderId = UUID.randomUUID();

        // Create mock Order
        mockOrder = new Order();
        mockOrder.setId(orderId);
        mockOrder.setUserId(userId);
        mockOrder.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

        // Create mock OrderLine
        mockOrderLine = new OrderLines();
        mockOrderLine.setId(1L);
        mockOrderLine.setOrder(mockOrder);
        mockOrderLine.setProductId(1L);
        mockOrderLine.setQuantity(2);
        mockOrderLine.setPrice(new BigDecimal("19.99"));

        // Create mock CartItem
        CartItem cartItem = new CartItem();
        cartItem.setProductId(1L);
        cartItem.setQuantity(2);
        cartItem.setPrice(new BigDecimal("19.99"));

        mockCartItems = Collections.singletonList(cartItem);

        // Set up default behavior for approveUser
        when(userRestTemplateClient.approveUser(anyString(), anyString())).thenReturn(true);

        // Set up mock OrderDto for mapper
        OrderDto mockOrderDto = mock(OrderDto.class);
        when(orderMapper.toOrderDto(any(Order.class), anyList())).thenReturn(mockOrderDto);
    }

    @Test
    void getAllOrders_ReturnsOrders_WhenOrdersExist() {
        // Arrange
        List<Order> orders = Collections.singletonList(mockOrder);
        when(orderRepository.findByUserId(userId)).thenReturn(orders);
        when(orderLineRepository.findByOrderId(orderId)).thenReturn(Collections.singletonList(mockOrderLine));

        // Act
        List<OrderDto> result = orderService.getAllOrders(userId);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(userRestTemplateClient).approveUser(anyString(), eq(userId.toString()));
        verify(orderRepository).findByUserId(userId);
        verify(orderLineRepository).findByOrderId(orderId);
        verify(orderMapper).toOrderDto(eq(mockOrder), anyList());
    }

    @Test
    void getAllOrders_ReturnsEmptyList_WhenNoOrdersExist() {
        // Arrange
        when(orderRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        // Act
        List<OrderDto> result = orderService.getAllOrders(userId);

        // Assert
        assertTrue(result.isEmpty());
        verify(userRestTemplateClient).approveUser(anyString(), eq(userId.toString()));
        verify(orderRepository).findByUserId(userId);
    }

    @Test
    void getAllOrders_ThrowsUnauthorizedAccessException_WhenUserNotApproved() {
        // Arrange
        when(userRestTemplateClient.approveUser(anyString(), eq(userId.toString()))).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () -> orderService.getAllOrders(userId));
        verify(userRestTemplateClient).approveUser(anyString(), eq(userId.toString()));
        verify(orderRepository, never()).findByUserId(any());
    }

    @Test
    void placeOrderFromCart_ReturnsOrderDto_WhenSuccessful() {
        // Arrange
        when(cartRestTemplateClient.getCartItems(userId)).thenReturn(mockCartItems);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(orderId);
            return order;
        });
        when(orderLineRepository.saveAll(anyList())).thenReturn(Collections.singletonList(mockOrderLine));

        // Act
        OrderDto result = orderService.placeOrderFromCart(userId);

        // Assert
        assertNotNull(result);
        verify(userRestTemplateClient).approveUser(anyString(), eq(userId.toString()));
        verify(cartRestTemplateClient).getCartItems(userId);
        verify(orderRepository).save(any(Order.class));
        verify(orderLineRepository).saveAll(anyList());
        verify(cartRestTemplateClient).archiveCart(userId);
        verify(orderMapper).toOrderDto(any(Order.class), anyList());
    }

    @Test
    void placeOrderFromCart_ThrowsCustomException_WhenCartIsEmpty() {
        // Arrange
        when(cartRestTemplateClient.getCartItems(userId)).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(CustomException.class, () -> orderService.placeOrderFromCart(userId));
        verify(userRestTemplateClient).approveUser(anyString(), eq(userId.toString()));
        verify(cartRestTemplateClient).getCartItems(userId);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrderFromCart_ThrowsUnauthorizedAccessException_WhenUserNotApproved() {
        // Arrange
        when(userRestTemplateClient.approveUser(anyString(), eq(userId.toString()))).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () -> orderService.placeOrderFromCart(userId));
        verify(userRestTemplateClient).approveUser(anyString(), eq(userId.toString()));
        verify(cartRestTemplateClient, never()).getCartItems(any());
    }

    @Test
    void placeOrder_ReturnsOrderDto_WhenSuccessful() {
        // Arrange
        Long productId = 1L;
        int quantity = 2;
        BigDecimal price = new BigDecimal("19.99");

        when(productRestTemplateClient.getPricing(productId)).thenReturn(price);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(orderId);
            return order;
        });
        when(orderLineRepository.save(any(OrderLines.class))).thenReturn(mockOrderLine);

        // Act
        OrderDto result = orderService.placeOrder(userId, productId, quantity);

        // Assert
        assertNotNull(result);
        verify(userRestTemplateClient).approveUser(anyString(), eq(userId.toString()));
        verify(productRestTemplateClient).getPricing(productId);
        verify(orderRepository).save(any(Order.class));
        verify(orderLineRepository).save(any(OrderLines.class));
        verify(orderMapper).toOrderDto(any(Order.class), anyList());
    }

    @Test
    void placeOrder_ThrowsCustomException_WhenQuantityIsZeroOrLess() {
        // Arrange
        Long productId = 1L;
        int quantity = 0;

        // Act & Assert
        assertThrows(CustomException.class, () -> orderService.placeOrder(userId, productId, quantity));
        verify(userRestTemplateClient).approveUser(anyString(), eq(userId.toString()));
        verify(productRestTemplateClient, never()).getPricing(any());
    }

    @Test
    void placeOrder_ThrowsCustomException_WhenPriceIsZero() {
        // Arrange
        Long productId = 1L;
        int quantity = 2;

        when(productRestTemplateClient.getPricing(productId)).thenReturn(BigDecimal.ZERO);

        // Act & Assert
        assertThrows(CustomException.class, () -> orderService.placeOrder(userId, productId, quantity));
        verify(userRestTemplateClient).approveUser(anyString(), eq(userId.toString()));
        verify(productRestTemplateClient).getPricing(productId);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrder_ThrowsUnauthorizedAccessException_WhenUserNotApproved() {
        // Arrange
        Long productId = 1L;
        int quantity = 2;

        when(userRestTemplateClient.approveUser(anyString(), eq(userId.toString()))).thenReturn(false);

        // Act & Assert
        assertThrows(CustomException.class, () -> orderService.placeOrder(userId, productId, quantity));
        verify(userRestTemplateClient).approveUser(anyString(), eq(userId.toString()));
        verify(productRestTemplateClient, never()).getPricing(any());
    }

    @Test
    void deleteOrder_Successful_WhenOrderExists() {
        // Arrange
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
        when(orderLineRepository.findByOrderId(orderId)).thenReturn(Collections.singletonList(mockOrderLine));
        doNothing().when(orderLineRepository).deleteAll(anyList());
        doNothing().when(orderRepository).delete(any(Order.class));

        // Act
        orderService.deleteOrder(orderId, userId);

        // Assert
        verify(userRestTemplateClient).approveUser(anyString(), eq(userId.toString()));
        verify(orderRepository).findById(orderId);
        verify(orderLineRepository).findByOrderId(orderId);
        verify(orderLineRepository).deleteAll(anyList());
        verify(orderRepository).delete(mockOrder);
    }

    @Test
    void deleteOrder_ThrowsCustomException_WhenOrderNotFound() {
        // Arrange
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomException.class, () -> orderService.deleteOrder(orderId, userId));
        verify(userRestTemplateClient).approveUser(anyString(), eq(userId.toString()));
        verify(orderRepository).findById(orderId);
        verify(orderLineRepository, never()).findByOrderId(any());
    }

    @Test
    void deleteOrder_ThrowsUnauthorizedAccessException_WhenOrderDoesNotBelongToUser() {
        // Arrange
        UUID differentUserId = UUID.randomUUID();
        Order orderWithDifferentUser = new Order();
        orderWithDifferentUser.setId(orderId);
        orderWithDifferentUser.setUserId(differentUserId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderWithDifferentUser));

        // Act & Assert
        assertThrows(CustomException.class, () -> orderService.deleteOrder(orderId, userId));
        verify(userRestTemplateClient).approveUser(anyString(), eq(userId.toString()));
        verify(orderRepository).findById(orderId);
        verify(orderLineRepository, never()).findByOrderId(any());
    }

    @Test
    void deleteOrder_ThrowsUnauthorizedAccessException_WhenUserNotApproved() {
        // Arrange
        when(userRestTemplateClient.approveUser(anyString(), eq(userId.toString()))).thenReturn(false);

        // Act & Assert
        assertThrows(CustomException.class, () -> orderService.deleteOrder(orderId, userId));
        verify(userRestTemplateClient).approveUser(anyString(), eq(userId.toString()));
        verify(orderRepository, never()).findById(any());
    }
}