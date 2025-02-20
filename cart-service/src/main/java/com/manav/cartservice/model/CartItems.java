package com.manav.cartservice.model;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "cart_items")
@IdClass(CartItemId.class)
public class CartItems {

    @Id
    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Carts cart;

    @Id
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price", nullable = false)
    private BigDecimal price;
}
