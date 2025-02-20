package com.manav.cartservice.controller;

import com.manav.cartservice.dto.AddCartItemRequest;
import com.manav.cartservice.dto.CartDto;
import com.manav.cartservice.dto.UpdateCartItemRequest;
import com.manav.cartservice.service.CartItemService;
import com.manav.cartservice.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final CartItemService cartItemService;

    public CartController(CartService cartService, CartItemService cartItemService) {
        this.cartService = cartService;
        this.cartItemService = cartItemService;
    }

    // Retrieve the active cart for a user
    @GetMapping("/{userId}")
    public ResponseEntity<CartDto> getCart(@PathVariable UUID userId) {
        return ResponseEntity.ok(cartService.getCartByUser(userId));
    }

    // Add an item to the cart
    @PostMapping("/{userId}/items")
    public ResponseEntity<CartDto> addItem(@PathVariable UUID userId,
                                           @RequestBody AddCartItemRequest request) {
        return ResponseEntity.ok(cartItemService.addItem(
                userId,
                request.getProductId(),
                request.getQuantity()
        ));
    }

    // Update an existing cart item
    @PutMapping("/{userId}/items")
    public ResponseEntity<CartDto> updateItem(@PathVariable UUID userId,
                                              @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartItemService.updateItem(
                userId,
                request.getProductId(),
                request.getQuantity()
        ));
    }

    // Remove an item from the cart
    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartDto> removeItem(@PathVariable UUID userId,
                                              @PathVariable Long productId) {
        return ResponseEntity.ok(cartItemService.removeItem(userId, productId));
    }

    // Checkout (archive) the cart
    @PostMapping("/{userId}/checkout")
    public ResponseEntity<CartDto> checkoutCart(@PathVariable UUID userId) {
        return ResponseEntity.ok(cartService.checkoutCart(userId));
    }
}
