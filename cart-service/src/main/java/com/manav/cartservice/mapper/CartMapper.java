package com.manav.cartservice.mapper;

import com.manav.cartservice.dto.CartDto;
import com.manav.cartservice.dto.CartItemDto;
import com.manav.cartservice.model.CartItems;
import com.manav.cartservice.model.Carts;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(source = "cart.id", target = "cartId")
    CartDto toCartDto(Carts cart, List<CartItemDto> items);

    CartItemDto toCartItemDto(CartItems cartItem);

    List<CartItemDto> toCartItemDtoList(List<CartItems> cartItems);
}