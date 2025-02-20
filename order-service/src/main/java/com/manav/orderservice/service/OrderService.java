package com.manav.orderservice.service;

import com.manav.orderservice.dto.OrderDto;
import com.manav.orderservice.dto.OrderLineDto;
import com.manav.orderservice.model.CartItem;
import com.manav.orderservice.model.Order;
import com.manav.orderservice.model.OrderLines;
import com.manav.orderservice.repository.OrderLineRepository;
import com.manav.orderservice.repository.OrderRepository;
import com.manav.orderservice.service.client.CartRestTemplateClient;
import com.manav.orderservice.service.client.ProductRestTemplateClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderLineRepository orderLineRepository;
    private final CartRestTemplateClient cartRestTemplateClient;
    private final ProductRestTemplateClient productRestTemplateClient;

    public OrderService(OrderRepository orderRepository,
                        OrderLineRepository orderLineRepository,
                        CartRestTemplateClient cartRestTemplateClient,
                        ProductRestTemplateClient productRestTemplateClient) {
        this.orderRepository = orderRepository;
        this.orderLineRepository = orderLineRepository;
        this.cartRestTemplateClient = cartRestTemplateClient;
        this.productRestTemplateClient = productRestTemplateClient;
    }

    public List<OrderDto> getAllOrders(UUID userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        if (orders.isEmpty()) return Collections.emptyList();

        return orders.stream()
                .map(order -> {
                    List<OrderLines> orderLines = getOrderLines(order.getId());
                    return convertToOrderDto(order, orderLines);
                })
                .toList();
    }

    public List<OrderLines> getOrderLines(UUID orderId) {
        List<OrderLines> orderLines = orderLineRepository.findByOrderId(orderId);
        return orderLines.isEmpty() ? Collections.emptyList() : orderLines;
    }

    @Transactional
    public OrderDto placeOrderFromCart(UUID userId) {

        // Get cart items from cart service
        List<CartItem> cartItems = cartRestTemplateClient.getCartItems(userId);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        // Create new order
        Order order = new Order();
        order.setUserId(userId);
        order.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        orderRepository.save(order);

        // Create order lines from cart items
        List<OrderLines> orderLinesList = cartItems.stream()
                .map(cartItem -> {
                    OrderLines orderLine = new OrderLines();
                    orderLine.setOrder(order);
                    orderLine.setProductId(cartItem.getProductId());
                    orderLine.setQuantity(cartItem.getQuantity());
                    orderLine.setPrice(cartItem.getPrice());
                    return orderLine;
                })
                .toList();


        orderLineRepository.saveAll(orderLinesList);

        // Archive the cart
        cartRestTemplateClient.archiveCart(userId);

        return convertToOrderDto(order, orderLinesList);
    }

    @Transactional
    public OrderDto placeOrder(UUID userId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        // Get product price from product service
        var price = productRestTemplateClient.getPricing(productId);
        if (price.equals(BigDecimal.ZERO)) {
            throw new IllegalStateException("Product price not available");
        }

        // Create new order
        Order order = new Order();
        order.setUserId(userId);
        order.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        orderRepository.save(order);

        // Create order line
        OrderLines orderLine = new OrderLines();
        orderLine.setOrder(order);
        orderLine.setProductId(productId);
        orderLine.setQuantity(quantity);
        orderLine.setPrice(price);
        orderLineRepository.save(orderLine);

        return convertToOrderDto(order, Collections.singletonList(orderLine));
    }

    private OrderDto convertToOrderDto(Order order, List<OrderLines> orderLines) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setUserId(order.getUserId());
        orderDto.setCreatedAt(order.getCreatedAt());

        List<OrderLineDto> orderLineDtos = orderLines.stream()
                .map(line -> {
                    OrderLineDto dto = new OrderLineDto();
                    dto.setProductId(line.getProductId());
                    dto.setQuantity(line.getQuantity());
                    dto.setPrice(line.getPrice());
                    return dto;
                })
                .toList();

        orderDto.setOrderLines(orderLineDtos);
        return orderDto;
    }

    @Transactional
    public void deleteOrder(UUID orderId, UUID userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found"));

        // Verify that the order belongs to the user
        if (!order.getUserId().equals(userId)) {
            throw new IllegalStateException("Order does not belong to the user");
        }

        // Delete all order lines first
        List<OrderLines> orderLines = orderLineRepository.findByOrderId(orderId);
        if (!orderLines.isEmpty()) {
            orderLineRepository.deleteAll(orderLines);
        }

        // Delete the order
        orderRepository.delete(order);
    }
}