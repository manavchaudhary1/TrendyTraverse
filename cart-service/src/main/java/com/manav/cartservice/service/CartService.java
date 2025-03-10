package com.manav.cartservice.service;

import com.manav.cartservice.dto.CartDto;
import com.manav.cartservice.dto.CartItemDto;
import com.manav.cartservice.exception.UnauthorizedAccessException;
import com.manav.cartservice.model.CartItems;
import com.manav.cartservice.model.Carts;
import com.manav.cartservice.repository.CartItemRepository;
import com.manav.cartservice.repository.CartRepository;
import com.manav.cartservice.service.client.UserRestTemplateClient;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import javax.smartcardio.CardNotPresentException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Validated
@Transactional
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRestTemplateClient userRestTemplateClient;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, UserRestTemplateClient userRestTemplateClient) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRestTemplateClient = userRestTemplateClient;
    }

    // Retrieves the active cart for the user (creates one if not found)
    public CartDto getCartByUser(UUID userId) {
        String username = getUsernameFromJwt();
        try {
            boolean isApproved = userRestTemplateClient.approveUser(username, userId.toString());
            if (!isApproved) {
                throw new UnauthorizedAccessException("Not authorized to access this cart.");
            }

            Carts cart = getCartEntityByUser(userId);
            List<CartItems> items = cartItemRepository.findByCart(cart);
            List<CartItemDto> itemDto = convertToCartItemDtoList(items);
            return convertToDto(cart, itemDto);

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found for user.");
        }
    }


    // Returns the cart entity for a user
    protected Carts getCartEntityByUser(UUID userId) {
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
    public CartDto checkoutCart(UUID userId) throws CardNotPresentException {
        Carts cart = cartRepository.findByUserIdAndArchivedFalse(userId);
        if (cart == null) {
            throw new CardNotPresentException("Active cart not found for user");
        }
        String username = getUsernameFromJwt();
        try{
            boolean isApproved = userRestTemplateClient.approveUser(username, userId.toString());
            if (!isApproved) {
                throw new UnauthorizedAccessException("Not authorized to access this cart.");
            }
            cart.setArchived(true);
            cart.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            cartRepository.save(cart);
            List<CartItems> items = cartItemRepository.findByCart(cart);
            List<CartItemDto> itemDto = convertToCartItemDtoList(items);
            return convertToDto(cart, itemDto);
        } catch (EntityNotFoundException e) {
            throw new UnauthorizedAccessException("Not authorized to access this cart.");
        }
    }

    // Updates the cart's updated_at timestamp
    protected void updateCartTimestamp(Carts cart) {
        cart.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        cartRepository.save(cart);
    }

    // Converts the cart entity and its items into a DTO
    protected CartDto convertToDto(Carts cart, List<CartItemDto> items) {
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
    protected List<CartItemDto> convertToCartItemDtoList(List<CartItems> cartItems) {
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


    protected String getUsernameFromJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) { // Handle JWT Authentication
            Jwt jwt = jwtAuth.getToken();
            return jwt.getClaim("preferred_username");  // Extract preferred_username
        }
        return null;
    }

}

