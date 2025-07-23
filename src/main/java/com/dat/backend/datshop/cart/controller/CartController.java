package com.dat.backend.datshop.cart.controller;

import com.dat.backend.datshop.cart.dto.AddOrRemoveProduct;
import com.dat.backend.datshop.cart.dto.CartItemResponse;
import com.dat.backend.datshop.cart.service.CartService;
import com.dat.backend.datshop.template.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    // Add product to cart
    @PostMapping("/add")
    public ApiResponse<String> addProductToCart(@RequestBody AddOrRemoveProduct addOrRemoveProduct, Authentication authentication) {
        return ApiResponse.success(cartService.addProductToCart(addOrRemoveProduct, authentication.getName()));
    }

    // Get cart of user
    @GetMapping("/")
    public ApiResponse<List<CartItemResponse>> getCart(Authentication authentication) {
        return ApiResponse.success(cartService.getCart(authentication.getName()));
    }

    // Remove product from cart
    @DeleteMapping("/remove")
    public ApiResponse<String> removeProductFromCart(@RequestBody AddOrRemoveProduct addOrRemoveProduct, Authentication authentication) {
       return ApiResponse.success(cartService.removeProductFromCart(addOrRemoveProduct, authentication.getName()));
    }
}
