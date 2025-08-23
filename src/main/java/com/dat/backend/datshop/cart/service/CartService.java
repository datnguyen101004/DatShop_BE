package com.dat.backend.datshop.cart.service;

import com.dat.backend.datshop.cart.dto.ProductItemRequest;
import com.dat.backend.datshop.cart.dto.CartItemResponse;
import com.dat.backend.datshop.cart.dto.UpdateCartProduct;

import java.util.List;

public interface CartService {
    String addProductToCart(ProductItemRequest productItemRequest, String email);

    List<CartItemResponse> getCart(String email);

    String removeProductFromCart(Long id, String name);

    String updateCartItemQuantity(UpdateCartProduct updateCartProduct, String name);

    String clearCart(String name);
}
