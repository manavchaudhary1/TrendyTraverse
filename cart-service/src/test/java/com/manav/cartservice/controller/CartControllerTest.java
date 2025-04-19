package com.manav.cartservice.controller;

import com.manav.cartservice.dto.AddCartItemRequest;
import com.manav.cartservice.dto.CartDto;
import com.manav.cartservice.dto.UpdateCartItemRequest;
import com.manav.cartservice.service.CartItemService;
import com.manav.cartservice.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.smartcardio.CardNotPresentException;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @Mock
    private CartItemService cartItemService;

    @InjectMocks
    private CartController cartController;

    private UUID userId;
    private Long productId;
    private CartDto mockCartDto;
    private AddCartItemRequest addRequest;
    private UpdateCartItemRequest updateRequest;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        productId = 1L;

        // Setup mock cart DTO
        mockCartDto = new CartDto();
        mockCartDto.setCartId(UUID.randomUUID());
        mockCartDto.setUserId(userId);
        mockCartDto.setItems(new ArrayList<>());

        // Setup add request
        addRequest = new AddCartItemRequest(productId);
        addRequest.setQuantity(2);

        // Setup update request
        updateRequest = new UpdateCartItemRequest(productId);
        updateRequest.setQuantity(5);
    }

    @Test
    void getCart_Success() {
        // Arrange
        when(cartService.getCartByUser(userId)).thenReturn(mockCartDto);

        // Act
        ResponseEntity<CartDto> response = cartController.getCart(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockCartDto, response.getBody());

        verify(cartService).getCartByUser(userId);
    }

    @Test
    void addItem_Success() {
        // Arrange
        when(cartItemService.addItem(userId, productId, 2)).thenReturn(mockCartDto);

        // Act
        ResponseEntity<CartDto> response = cartController.addItem(userId, addRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockCartDto, response.getBody());

        verify(cartItemService).addItem(userId, productId, 2);
    }

    @Test
    void updateItem_Success() {
        // Arrange
        when(cartItemService.updateItem(userId, productId, 5)).thenReturn(mockCartDto);

        // Act
        ResponseEntity<CartDto> response = cartController.updateItem(userId, updateRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockCartDto, response.getBody());

        verify(cartItemService).updateItem(userId, productId, 5);
    }

    @Test
    void removeItem_Success() {
        // Arrange
        when(cartItemService.removeItem(userId, productId)).thenReturn(mockCartDto);

        // Act
        ResponseEntity<CartDto> response = cartController.removeItem(userId, productId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockCartDto, response.getBody());

        verify(cartItemService).removeItem(userId, productId);
    }

    @Test
    void checkoutCart_Success() throws CardNotPresentException {
        // Arrange
        when(cartService.checkoutCart(userId)).thenReturn(mockCartDto);

        // Act
        ResponseEntity<CartDto> response = cartController.checkoutCart(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockCartDto, response.getBody());

        verify(cartService).checkoutCart(userId);
    }

    @Test
    void checkoutCart_CardNotPresent() throws CardNotPresentException {
        // Arrange
        when(cartService.checkoutCart(userId)).thenThrow(new CardNotPresentException("Active cart not found for user"));

        // Act & Assert
        assertThrows(CardNotPresentException.class, () -> cartController.checkoutCart(userId));
        verify(cartService).checkoutCart(userId);
    }
}