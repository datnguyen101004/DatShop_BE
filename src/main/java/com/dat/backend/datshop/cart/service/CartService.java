package com.dat.backend.datshop.cart.service;

import com.dat.backend.datshop.cart.dto.AddProduct;
import com.dat.backend.datshop.cart.dto.CartResponse;

public interface CartService {
    String addProductToCart(AddProduct addProduct, String email);

    CartResponse getCart(String email);
}
