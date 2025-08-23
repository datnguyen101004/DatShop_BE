package com.dat.backend.datshop.cart.controller;

import com.dat.backend.datshop.cart.dto.ProductItemRequest;
import com.dat.backend.datshop.cart.dto.CartItemResponse;
import com.dat.backend.datshop.cart.dto.UpdateCartProduct;
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
    public ApiResponse<String> addProductToCart(@RequestBody ProductItemRequest productItemRequest, Authentication authentication) {
        return ApiResponse.success(cartService.addProductToCart(productItemRequest, authentication.getName()));
    }

    // Get cart of user
    @GetMapping("/")
    public ApiResponse<List<CartItemResponse>> getCart(Authentication authentication) {
        return ApiResponse.success(cartService.getCart(authentication.getName()));
    }

    // Remove product from cart
    @DeleteMapping("/{id}")
    public ApiResponse<String> removeProductFromCart(@PathVariable Long id, Authentication authentication) {
       return ApiResponse.success(cartService.removeProductFromCart(id, authentication.getName()));
    }

    // Update cart item quantity
    @PutMapping("/update")
    public ApiResponse<String> updateCartItemQuantity(@RequestBody UpdateCartProduct updateCartProduct, Authentication authentication) {
        return ApiResponse.success(cartService.updateCartItemQuantity(updateCartProduct, authentication.getName()));
    }

    // Clear cart
    @DeleteMapping("/clear")
    public ApiResponse<String> clearCart(Authentication authentication) {
        return ApiResponse.success(cartService.clearCart(authentication.getName()));
    }
}
