package com.manav.cartservice.service;

import com.manav.cartservice.dto.CartDto;
import com.manav.cartservice.dto.CartItemDto;
import com.manav.cartservice.mapper.CartMapper;
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

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRestTemplateClient userRestTemplateClient;

    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartService cartService;

    private UUID userId;
    private UUID cartId;
    private Carts mockCart;
    private List<CartItems> mockCartItems;
    private List<CartItemDto> mockCartItemDtos;
    private CartDto mockCartDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        cartId = UUID.randomUUID();

        // Setup cart
        mockCart = new Carts();
        mockCart.setId(cartId);
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

        // Setup cart item DTOs
        mockCartItemDtos = new ArrayList<>();
        mockCartItemDtos.add(new CartItemDto(1L, 2, BigDecimal.valueOf(19.99)));

        // Setup cart DTO
        mockCartDto = new CartDto(
                cartId,
                userId,
                mockCart.getCreatedAt(),
                mockCart.getUpdatedAt(),
                false,
                mockCartItemDtos
        );

        cartService = spy(new CartService(cartRepository, cartItemRepository, userRestTemplateClient, cartMapper));
    }

    @Test
    void getCartByUser_Success() {
        // Arrange
        doReturn("testUser").when(cartService).getUsernameFromJwt();
        when(userRestTemplateClient.approveUser("testUser", userId.toString())).thenReturn(true);
        when(cartRepository.findByUserIdAndArchivedFalse(userId)).thenReturn(mockCart);
        when(cartItemRepository.findByCart(mockCart)).thenReturn(mockCartItems);
        when(cartMapper.toCartItemDtoList(mockCartItems)).thenReturn(mockCartItemDtos);
        when(cartMapper.toCartDto(mockCart, mockCartItemDtos)).thenReturn(mockCartDto);

        // Act
        CartDto result = cartService.getCartByUser(userId);

        // Assert
        assertNotNull(result);
        assertEquals(cartId, result.cartId());
        assertEquals(userId, result.userId());
        assertEquals(1, result.items().size());

        verify(cartRepository).findByUserIdAndArchivedFalse(userId);
        verify(cartItemRepository).findByCart(mockCart);
        verify(cartMapper).toCartItemDtoList(mockCartItems);
        verify(cartMapper).toCartDto(mockCart, mockCartItemDtos);
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
    void checkoutCart_Success() throws CardNotPresentException {
        // Arrange
        when(cartRepository.findByUserIdAndArchivedFalse(userId)).thenReturn(mockCart);
        doReturn("testUser").when(cartService).getUsernameFromJwt();
        when(userRestTemplateClient.approveUser("testUser", userId.toString())).thenReturn(true);
        when(cartItemRepository.findByCart(mockCart)).thenReturn(mockCartItems);
        when(cartMapper.toCartItemDtoList(mockCartItems)).thenReturn(mockCartItemDtos);
        when(cartMapper.toCartDto(mockCart, mockCartItemDtos)).thenReturn(mockCartDto);

        // Act
        CartDto result = cartService.checkoutCart(userId);

        // Assert
        assertTrue(mockCart.isArchived());
        assertNotNull(result);
        verify(cartRepository).save(mockCart);
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
        Timestamp before = mockCart.getUpdatedAt();

        // Act
        cartService.updateCartTimestamp(mockCart);

        // Assert
        assertNotNull(mockCart.getUpdatedAt());
        assertNotEquals(before, mockCart.getUpdatedAt());
        verify(cartRepository).save(mockCart);
    }
}