package com.manav.cartservice.repository;

import com.manav.cartservice.model.CartItemId;
import com.manav.cartservice.model.CartItems;
import com.manav.cartservice.model.Carts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItems, CartItemId> {
    List<CartItems> findByCart(Carts cart);
    Optional<CartItems> findByCartAndProductId(Carts cart, Long productId);
}
