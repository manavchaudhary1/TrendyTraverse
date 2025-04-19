package com.manav.cartservice.service;

import com.manav.cartservice.dto.CartDto;
import com.manav.cartservice.dto.CartItemDto;
import com.manav.cartservice.exception.UnauthorizedAccessException;
import com.manav.cartservice.model.CartItems;
import com.manav.cartservice.model.Carts;
import com.manav.cartservice.repository.CartItemRepository;
import com.manav.cartservice.repository.CartRepository;
import com.manav.cartservice.service.client.UserRestTemplateClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import javax.smartcardio.CardNotPresentException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;


    @InjectMocks
    private CartService cartService;

    private UUID userId;
    private Carts mockCart;
    private List<CartItems> mockCartItems;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        // Setup cart
        mockCart = new Carts();
        mockCart.setId(UUID.randomUUID());
        mockCart.setUserId(userId);
        mockCart.setCreatedAt(Timestamp.from(Instant.now()));
        mockCart.setUpdatedAt(Timestamp.from(Instant.now()));
        mockCart.setArchived(false);

        // Setup cart items
        mockCartItems = new ArrayList<>();
        CartItems item = new CartItems();
        item.setCart(mockCart);
        item.setProductId(1L);
        item.setQuantity(2);
        item.setPrice(BigDecimal.valueOf(19.99));
        mockCartItems.add(item);
    }

    @Test
    void getCartEntityByUser_CartExists() {
        // Arrange
        when(cartRepository.findByUserIdAndArchivedFalse(userId)).thenReturn(mockCart);

        // Act
        Carts result = cartService.getCartEntityByUser(userId);

        // Assert
        assertNotNull(result);
        assertEquals(mockCart.getId(), result.getId());
        assertEquals(mockCart.getUserId(), result.getUserId());
        verify(cartRepository).findByUserIdAndArchivedFalse(userId);
        verify(cartRepository, never()).save(any());
    }

    @Test
    void getCartEntityByUser_CreateNewCart() {
        // Arrange
        when(cartRepository.findByUserIdAndArchivedFalse(userId)).thenReturn(null);
        when(cartRepository.save(any(Carts.class))).thenReturn(mockCart);

        // Act
        Carts result = cartService.getCartEntityByUser(userId);

        // Assert
        assertNotNull(result);
        assertEquals(mockCart.getId(), result.getId());
        verify(cartRepository).findByUserIdAndArchivedFalse(userId);
        verify(cartRepository).save(any(Carts.class));
    }

    @Test
    void createNewCart_Success() {
        // Arrange
        when(cartRepository.save(any(Carts.class))).thenReturn(mockCart);

        // Act
        Carts result = cartService.createNewCart(userId);

        // Assert
        assertNotNull(result);
        assertEquals(mockCart.getId(), result.getId());
        verify(cartRepository).save(any(Carts.class));
    }

    @Test
    void checkoutCart_CartNotFound() {
        // Arrange
        when(cartRepository.findByUserIdAndArchivedFalse(userId)).thenReturn(null);

        // Act & Assert
        assertThrows(CardNotPresentException.class, () -> cartService.checkoutCart(userId));
        verify(cartRepository).findByUserIdAndArchivedFalse(userId);
        verify(cartRepository, never()).save(any());
    }

    @Test
    void updateCartTimestamp_Success() {
        // Arrange
        when(cartRepository.save(mockCart)).thenReturn(mockCart);

        // Act
        cartService.updateCartTimestamp(mockCart);

        // Assert
        assertNotNull(mockCart.getUpdatedAt());
        verify(cartRepository).save(mockCart);
    }

    @Test
    void convertToDto_Success() {
        // Arrange
        List<CartItemDto> itemDtos = cartService.convertToCartItemDtoList(mockCartItems);

        // Act
        CartDto result = cartService.convertToDto(mockCart, itemDtos);

        // Assert
        assertNotNull(result);
        assertEquals(mockCart.getId(), result.getCartId());
        assertEquals(mockCart.getUserId(), result.getUserId());
        assertEquals(mockCart.getCreatedAt(), result.getCreatedAt());
        assertEquals(mockCart.getUpdatedAt(), result.getUpdatedAt());
        assertEquals(mockCart.isArchived(), result.isArchived());
        assertEquals(1, result.getItems().size());
    }

    @Test
    void convertToCartItemDtoList_Success() {
        // Act
        List<CartItemDto> result = cartService.convertToCartItemDtoList(mockCartItems);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getProductId());
        assertEquals(2, result.getFirst().getQuantity());
        assertEquals(BigDecimal.valueOf(19.99), result.getFirst().getPrice());
    }
}