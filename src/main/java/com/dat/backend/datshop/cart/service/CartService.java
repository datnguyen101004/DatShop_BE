package com.dat.backend.datshop.cart.service;

import com.dat.backend.datshop.cart.dto.AddOrRemoveProduct;
import com.dat.backend.datshop.cart.dto.CartItemResponse;

import java.util.List;

public interface CartService {
    String addProductToCart(AddOrRemoveProduct addOrRemoveProduct, String email);

    List<CartItemResponse> getCart(String email);

    String removeProductFromCart(AddOrRemoveProduct addOrRemoveProduct, String name);
}
