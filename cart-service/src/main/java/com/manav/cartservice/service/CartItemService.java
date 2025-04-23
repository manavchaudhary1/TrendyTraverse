package com.manav.cartservice.service;

import com.manav.cartservice.dto.CartDto;
import com.manav.cartservice.dto.CartItemDto;
import com.manav.cartservice.exception.CustomException;
import com.manav.cartservice.exception.UnauthorizedAccessException;
import com.manav.cartservice.mapper.CartMapper;
import com.manav.cartservice.model.CartItems;
import com.manav.cartservice.model.Carts;
import com.manav.cartservice.repository.CartItemRepository;
import com.manav.cartservice.service.client.ProductRestTemplateClient;
import com.manav.cartservice.service.client.UserRestTemplateClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartService cartService;
    private final ProductRestTemplateClient productRestTemplateClient;
    private final UserRestTemplateClient userRestTemplateClient;
    private final CartMapper cartMapper;

    public CartDto addItem(UUID userId, Long productId, int quantity) {
        String username = cartService.getUsernameFromJwt();
        try {
            boolean isApproved = userRestTemplateClient.approveUser(username, userId.toString());
            if (!isApproved) {
                throw new UnauthorizedAccessException("Not authorized to access this cart.");
            }

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
            List<CartItemDto> itemDtos = cartMapper.toCartItemDtoList(items);
            return cartMapper.toCartDto(cart, itemDtos);
        } catch (HttpClientErrorException e) {
            log.error("Error from product service: {}", e.getMessage());
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new CustomException("Product not found: " + productId);
            }
            throw new CustomException("Error from product service: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error adding item to cart", e);
            throw new CustomException("Error adding item to cart: " + e.getMessage());
        }
    }

    // Updates an existing cart item's quantity and/or price
    public CartDto updateItem(UUID userId, Long productId, int quantity) {
        String username = cartService.getUsernameFromJwt();
        boolean isApproved = userRestTemplateClient.approveUser(username, userId.toString());

        if (!isApproved) {
            throw new UnauthorizedAccessException("Not authorized to access this cart.");
        }

        Carts cart = cartService.getCartEntityByUser(userId);
        CartItems item = cartItemRepository.findByCartAndProductId(cart, productId)
                .orElseThrow(() -> new CustomException("Item not found in cart"));

        item.setQuantity(quantity);
        item.setPrice(BigDecimal.valueOf(quantity).multiply(productRestTemplateClient.getPricing(productId)));
        cartItemRepository.save(item);
        cartService.updateCartTimestamp(cart);

        List<CartItems> items = cartItemRepository.findByCart(cart);
        List<CartItemDto> itemDtos = cartMapper.toCartItemDtoList(items);
        return cartMapper.toCartDto(cart, itemDtos);
    }

    // Removes an item from the cart
    public CartDto removeItem(UUID userId, Long productId) {
        String username = cartService.getUsernameFromJwt();
        boolean isApproved = userRestTemplateClient.approveUser(username, userId.toString());

        if (!isApproved) {
            throw new UnauthorizedAccessException("Not authorized to access this cart.");
        }

        Carts cart = cartService.getCartEntityByUser(userId);
        CartItems item = cartItemRepository.findByCartAndProductId(cart, productId)
                .orElseThrow(() -> new CustomException("Item not found in cart"));

        cartItemRepository.delete(item);
        cartService.updateCartTimestamp(cart);

        List<CartItems> items = cartItemRepository.findByCart(cart);
        List<CartItemDto> itemDtos = cartMapper.toCartItemDtoList(items);
        return cartMapper.toCartDto(cart, itemDtos);
    }
}