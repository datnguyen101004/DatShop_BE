package com.dat.backend.datshop.cart.controller;

import com.dat.backend.datshop.cart.dto.AddProduct;
import com.dat.backend.datshop.cart.dto.CartResponse;
import com.dat.backend.datshop.cart.entity.CartItem;
import com.dat.backend.datshop.cart.service.CartService;
import com.dat.backend.datshop.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    // Add product to cart
    @PostMapping("/add")
    public ApiResponse<String> addProductToCart(@RequestBody AddProduct addProduct, Authentication authentication) {
        return ApiResponse.success(cartService.addProductToCart(addProduct, authentication.getName()));
    }

    // Get cart of user
    @GetMapping("/")
    public ApiResponse<CartResponse> getCart(Authentication authentication) {
        return ApiResponse.success(cartService.getCart(authentication.getName()));
    }

}
