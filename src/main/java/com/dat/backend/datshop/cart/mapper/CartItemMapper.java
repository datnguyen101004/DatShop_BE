package com.dat.backend.datshop.cart.mapper;

import com.dat.backend.datshop.cart.dto.CartItemResponse;
import com.dat.backend.datshop.cart.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    @Mapping(target = "cartItemId", source = "id")
    CartItemResponse mapToCartItemResponse(CartItem cartItem);
}
