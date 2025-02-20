package com.manav.cartservice.service;

import com.manav.cartservice.dto.CartDto;
import com.manav.cartservice.model.CartItems;
import com.manav.cartservice.model.Carts;
import com.manav.cartservice.repository.CartItemRepository;
import com.manav.cartservice.service.client.ProductRestTemplateClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartService cartService;
    private final ProductRestTemplateClient productRestTemplateClient;

    public CartItemService(CartItemRepository cartItemRepository, CartService cartService, ProductRestTemplateClient productRestTemplateClient) {
        this.cartItemRepository = cartItemRepository;
        this.cartService = cartService;
        this.productRestTemplateClient = productRestTemplateClient;
    }

    public CartDto addItem(UUID userId, Long productId, int quantity) {
        // Get the cart entity
        Carts cart = cartService.getCartEntityByUser(userId);

        // Find existing cart item or create new one
        CartItems item = cartItemRepository.findByCartAndProductId(cart, productId)
                .map(existingItem -> {
                    // Update existing item
                    existingItem.setQuantity(existingItem.getQuantity() + quantity);
                    existingItem.setPrice(productRestTemplateClient.getPricing(productId));
                    return existingItem;
                })
                .orElseGet(() -> {
                    // Create new item
                    CartItems newItem = new CartItems();
                    newItem.setCart(cart);
                    newItem.setProductId(productId);
                    newItem.setQuantity(quantity);
                    newItem.setPrice(BigDecimal.valueOf(quantity).multiply(productRestTemplateClient.getPricing(productId)));
                    return newItem;
                });

        // Save the item
        cartItemRepository.save(item);

        // Update cart timestamp
        cartService.updateCartTimestamp(cart);

        // Get updated items and convert to DTO
        List<CartItems> items = cartItemRepository.findByCart(cart);
        return cartService.convertToDto(cart, cartService.convertToCartItemDtoList(items));
    }

    // Updates an existing cart item's quantity and/or price
    public CartDto updateItem(UUID userId, Long productId, int quantity) {
        Carts cart = cartService.getCartEntityByUser(userId);
        CartItems item = cartItemRepository.findByCartAndProductId(cart, productId)
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));
        item.setQuantity(quantity);
        item.setPrice(BigDecimal.valueOf(quantity).multiply(productRestTemplateClient.getPricing(productId)));
        cartItemRepository.save(item);
        cartService.updateCartTimestamp(cart);
        List<CartItems> items = cartItemRepository.findByCart(cart);
        return cartService.convertToDto(cart, cartService.convertToCartItemDtoList(items));
    }

    // Removes an item from the cart
    public CartDto removeItem(UUID userId, Long productId) {
        Carts cart = cartService.getCartEntityByUser(userId);
        CartItems item = cartItemRepository.findByCartAndProductId(cart, productId)
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));
        cartItemRepository.delete(item);
        cartService.updateCartTimestamp(cart);
        List<CartItems> items = cartItemRepository.findByCart(cart);
        return cartService.convertToDto(cart, cartService.convertToCartItemDtoList(items));
    }
}
