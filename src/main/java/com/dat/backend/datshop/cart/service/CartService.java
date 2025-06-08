package com.dat.backend.datshop.cart.service;

import com.dat.backend.datshop.cart.dto.AddOrRemoveProduct;
import com.dat.backend.datshop.cart.dto.CartResponse;

public interface CartService {
    String addProductToCart(AddOrRemoveProduct addOrRemoveProduct, String email);

    CartResponse getCart(String email);

    String removeProductFromCart(AddOrRemoveProduct addOrRemoveProduct, String name);
}
