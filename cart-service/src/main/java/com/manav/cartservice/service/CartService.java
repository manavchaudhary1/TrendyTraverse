package com.manav.cartservice.service;

import com.manav.cartservice.dto.CartDto;
import com.manav.cartservice.dto.CartItemDto;
import com.manav.cartservice.model.CartItems;
import com.manav.cartservice.model.Carts;
import com.manav.cartservice.repository.CartItemRepository;
import com.manav.cartservice.repository.CartRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Validated
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    // Retrieves the active cart for the user (creates one if not found)
    public CartDto getCartByUser(UUID userId) {
        Carts cart = getCartEntityByUser(userId);
        List<CartItems> items = cartItemRepository.findByCart(cart);
        List<CartItemDto> itemDto = convertToCartItemDtoList(items);
        return convertToDto(cart, itemDto);
    }

    // Returns the cart entity for a user
    public Carts getCartEntityByUser(UUID userId) {
        Carts cart = cartRepository.findByUserIdAndArchivedFalse(userId);
        if (cart == null) {
            cart = createNewCart(userId);
        }
        return cart;
    }

    // Creates a new cart for the user
    public Carts createNewCart(UUID userId) {
        Carts cart = new Carts();
        cart.setUserId(userId);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        cart.setCreatedAt(now);
        cart.setUpdatedAt(now);
        cart.setArchived(false);
        return cartRepository.save(cart);
    }

    // Checks out (archives) the cart
    public CartDto checkoutCart(UUID userId) {
        Carts cart = cartRepository.findByUserIdAndArchivedFalse(userId);
        if (cart == null) {
            throw new IllegalArgumentException("Active cart not found for user");
        }
        cart.setArchived(true);
        cart.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        cartRepository.save(cart);
        List<CartItems> items = cartItemRepository.findByCart(cart);
        List<CartItemDto> itemDto = convertToCartItemDtoList(items);
        return convertToDto(cart, itemDto);
    }

    // Updates the cart's updated_at timestamp
    public void updateCartTimestamp(Carts cart) {
        cart.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        cartRepository.save(cart);
    }

    // Converts the cart entity and its items into a DTO
    public CartDto convertToDto(Carts cart, List<CartItemDto> items) {
        CartDto dto = new CartDto();
        dto.setCartId(cart.getId());
        dto.setUserId(cart.getUserId());
        dto.setCreatedAt(cart.getCreatedAt());
        dto.setUpdatedAt(cart.getUpdatedAt());
        dto.setArchived(cart.isArchived());
        dto.setItems(items);
        return dto;
    }
    // Converts CartItems list into CartItemDto list
    public List<CartItemDto> convertToCartItemDtoList(List<CartItems> cartItems) {
        List<CartItemDto> itemDto = new ArrayList<>();
        for (CartItems item : cartItems) {
            CartItemDto dto = new CartItemDto();
            dto.setProductId(item.getProductId());
            dto.setQuantity(item.getQuantity());
            dto.setPrice(item.getPrice());
            itemDto.add(dto);
        }
        return itemDto;
    }
}

