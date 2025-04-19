package com.manav.cartservice.service;

import com.manav.cartservice.dto.CartDto;
import com.manav.cartservice.exception.CustomException;
import com.manav.cartservice.exception.UnauthorizedAccessException;
import com.manav.cartservice.model.CartItems;
import com.manav.cartservice.model.Carts;
import com.manav.cartservice.repository.CartItemRepository;
import com.manav.cartservice.service.client.ProductRestTemplateClient;
import com.manav.cartservice.service.client.UserRestTemplateClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartService cartService;

    @Mock
    private ProductRestTemplateClient productRestTemplateClient;

    @Mock
    private UserRestTemplateClient userRestTemplateClient;

    @InjectMocks
    private CartItemService cartItemService;

    private UUID userId;
    private Long productId;
    private String username;
    private Carts mockCart;
    private CartItems mockCartItem;
    private List<CartItems> mockCartItems;
    private CartDto mockCartDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        productId = 1L;
        username = "testUser";

        // Setup cart
        mockCart = new Carts();
        mockCart.setId(UUID.randomUUID());
        mockCart.setUserId(userId);

        // Setup cart item
        mockCartItem = new CartItems();
        mockCartItem.setCart(mockCart);
        mockCartItem.setProductId(productId);
        mockCartItem.setQuantity(2);
        mockCartItem.setPrice(BigDecimal.valueOf(19.99));

        // Setup cart items list
        mockCartItems = new ArrayList<>();
        mockCartItems.add(mockCartItem);

        // Setup cart DTO
        mockCartDto = new CartDto();
        mockCartDto.setCartId(mockCart.getId());
        mockCartDto.setUserId(userId);
    }

    @Test
    void addItem_NewItem_Success() {
        // Arrange
        when(cartService.getUsernameFromJwt()).thenReturn(username);
        when(userRestTemplateClient.approveUser(username, userId.toString())).thenReturn(true);
        when(cartService.getCartEntityByUser(userId)).thenReturn(mockCart);
        when(cartItemRepository.findByCartAndProductId(mockCart, productId)).thenReturn(Optional.empty());
        when(productRestTemplateClient.getPricing(productId)).thenReturn(BigDecimal.valueOf(9.99));
        when(cartItemRepository.save(any(CartItems.class))).thenReturn(mockCartItem);
        when(cartItemRepository.findByCart(mockCart)).thenReturn(mockCartItems);
        when(cartService.convertToCartItemDtoList(mockCartItems)).thenReturn(new ArrayList<>());
        when(cartService.convertToDto(eq(mockCart), any())).thenReturn(mockCartDto);

        // Act
        CartDto result = cartItemService.addItem(userId, productId, 2);

        // Assert
        assertNotNull(result);
        assertEquals(mockCart.getId(), result.getCartId());

        verify(cartService).getUsernameFromJwt();
        verify(userRestTemplateClient).approveUser(username, userId.toString());
        verify(cartService).getCartEntityByUser(userId);
        verify(cartItemRepository).findByCartAndProductId(mockCart, productId);
        verify(productRestTemplateClient).getPricing(productId);
        verify(cartItemRepository).save(any(CartItems.class));
        verify(cartService).updateCartTimestamp(mockCart);
        verify(cartItemRepository).findByCart(mockCart);
        verify(cartService).convertToCartItemDtoList(mockCartItems);
        verify(cartService).convertToDto(eq(mockCart), any());
    }

    @Test
    void addItem_ExistingItem_Success() {
        // Arrange
        when(cartService.getUsernameFromJwt()).thenReturn(username);
        when(userRestTemplateClient.approveUser(username, userId.toString())).thenReturn(true);
        when(cartService.getCartEntityByUser(userId)).thenReturn(mockCart);
        when(cartItemRepository.findByCartAndProductId(mockCart, productId)).thenReturn(Optional.of(mockCartItem));
        when(productRestTemplateClient.getPricing(productId)).thenReturn(BigDecimal.valueOf(9.99));
        when(cartItemRepository.save(mockCartItem)).thenReturn(mockCartItem);
        when(cartItemRepository.findByCart(mockCart)).thenReturn(mockCartItems);
        when(cartService.convertToCartItemDtoList(mockCartItems)).thenReturn(new ArrayList<>());
        when(cartService.convertToDto(eq(mockCart), any())).thenReturn(mockCartDto);

        // Act
        CartDto result = cartItemService.addItem(userId, productId, 1);

        // Assert
        assertNotNull(result);
        assertEquals(mockCart.getId(), result.getCartId());
        assertEquals(3, mockCartItem.getQuantity()); // Original 2 + 1 new

        verify(cartService).getUsernameFromJwt();
        verify(userRestTemplateClient).approveUser(username, userId.toString());
        verify(cartService).getCartEntityByUser(userId);
        verify(cartItemRepository).findByCartAndProductId(mockCart, productId);
        verify(productRestTemplateClient).getPricing(productId);
        verify(cartItemRepository).save(mockCartItem);
        verify(cartService).updateCartTimestamp(mockCart);
        verify(cartItemRepository).findByCart(mockCart);
    }

    @Test
    void updateItem_Success() {
        // Arrange
        when(cartService.getUsernameFromJwt()).thenReturn(username);
        when(userRestTemplateClient.approveUser(username, userId.toString())).thenReturn(true);
        when(cartService.getCartEntityByUser(userId)).thenReturn(mockCart);
        when(cartItemRepository.findByCartAndProductId(mockCart, productId)).thenReturn(Optional.of(mockCartItem));
        when(productRestTemplateClient.getPricing(productId)).thenReturn(BigDecimal.valueOf(9.99));
        when(cartItemRepository.save(mockCartItem)).thenReturn(mockCartItem);
        when(cartItemRepository.findByCart(mockCart)).thenReturn(mockCartItems);
        when(cartService.convertToCartItemDtoList(mockCartItems)).thenReturn(new ArrayList<>());
        when(cartService.convertToDto(eq(mockCart), any())).thenReturn(mockCartDto);

        // Act
        CartDto result = cartItemService.updateItem(userId, productId, 5);

        // Assert
        assertNotNull(result);
        assertEquals(5, mockCartItem.getQuantity());
        assertEquals(BigDecimal.valueOf(5).multiply(BigDecimal.valueOf(9.99)), mockCartItem.getPrice());

        verify(cartService).getUsernameFromJwt();
        verify(userRestTemplateClient).approveUser(username, userId.toString());
        verify(cartService).getCartEntityByUser(userId);
        verify(cartItemRepository).findByCartAndProductId(mockCart, productId);
        verify(productRestTemplateClient).getPricing(productId);
        verify(cartItemRepository).save(mockCartItem);
        verify(cartService).updateCartTimestamp(mockCart);
    }

    @Test
    void updateItem_ItemNotFound() {
        // Arrange
        when(cartService.getUsernameFromJwt()).thenReturn(username);
        when(userRestTemplateClient.approveUser(username, userId.toString())).thenReturn(true);
        when(cartService.getCartEntityByUser(userId)).thenReturn(mockCart);
        when(cartItemRepository.findByCartAndProductId(mockCart, productId)).thenReturn(Optional.empty());

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> cartItemService.updateItem(userId, productId, 5));
        assertEquals("Item not found in cart", exception.getMessage());

        verify(cartService).getUsernameFromJwt();
        verify(userRestTemplateClient).approveUser(username, userId.toString());
        verify(cartService).getCartEntityByUser(userId);
        verify(cartItemRepository).findByCartAndProductId(mockCart, productId);
        verifyNoMoreInteractions(cartItemRepository);
        verifyNoInteractions(productRestTemplateClient);
    }

    @Test
    void updateItem_Unauthorized() {
        // Arrange
        when(cartService.getUsernameFromJwt()).thenReturn(username);
        when(userRestTemplateClient.approveUser(username, userId.toString())).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () -> cartItemService.updateItem(userId, productId, 5));

        verify(cartService).getUsernameFromJwt();
        verify(userRestTemplateClient).approveUser(username, userId.toString());
        verifyNoInteractions(cartItemRepository);
    }

    @Test
    void removeItem_Success() {
        // Arrange
        when(cartService.getUsernameFromJwt()).thenReturn(username);
        when(userRestTemplateClient.approveUser(username, userId.toString())).thenReturn(true);
        when(cartService.getCartEntityByUser(userId)).thenReturn(mockCart);
        when(cartItemRepository.findByCartAndProductId(mockCart, productId)).thenReturn(Optional.of(mockCartItem));
        when(cartItemRepository.findByCart(mockCart)).thenReturn(new ArrayList<>());
        when(cartService.convertToCartItemDtoList(any())).thenReturn(new ArrayList<>());
        when(cartService.convertToDto(eq(mockCart), any())).thenReturn(mockCartDto);

        // Act
        CartDto result = cartItemService.removeItem(userId, productId);

        // Assert
        assertNotNull(result);

        verify(cartService).getUsernameFromJwt();
        verify(userRestTemplateClient).approveUser(username, userId.toString());
        verify(cartService).getCartEntityByUser(userId);
        verify(cartItemRepository).findByCartAndProductId(mockCart, productId);
        verify(cartItemRepository).delete(mockCartItem);
        verify(cartService).updateCartTimestamp(mockCart);
        verify(cartItemRepository).findByCart(mockCart);
    }

    @Test
    void removeItem_ItemNotFound() {
        // Arrange
        when(cartService.getUsernameFromJwt()).thenReturn(username);
        when(userRestTemplateClient.approveUser(username, userId.toString())).thenReturn(true);
        when(cartService.getCartEntityByUser(userId)).thenReturn(mockCart);
        when(cartItemRepository.findByCartAndProductId(mockCart, productId)).thenReturn(Optional.empty());

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> cartItemService.removeItem(userId, productId));
        assertEquals("Item not found in cart", exception.getMessage());

        verify(cartService).getUsernameFromJwt();
        verify(userRestTemplateClient).approveUser(username, userId.toString());
        verify(cartService).getCartEntityByUser(userId);
        verify(cartItemRepository).findByCartAndProductId(mockCart, productId);
        verifyNoMoreInteractions(cartItemRepository);
    }

    @Test
    void removeItem_Unauthorized() {
        // Arrange
        when(cartService.getUsernameFromJwt()).thenReturn(username);
        when(userRestTemplateClient.approveUser(username, userId.toString())).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () -> cartItemService.removeItem(userId, productId));

        verify(cartService).getUsernameFromJwt();
        verify(userRestTemplateClient).approveUser(username, userId.toString());
        verifyNoInteractions(cartItemRepository);
    }
}