package com.dat.backend.datshop.production.controller;

import com.dat.backend.datshop.production.dto.CreateOrEditProduct;
import com.dat.backend.datshop.production.dto.ProductResponse;
import com.dat.backend.datshop.production.service.ProductService;
import com.dat.backend.datshop.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    // Add new product
    @PostMapping("/add")
    public ApiResponse<ProductResponse> addProduct(@RequestBody CreateOrEditProduct createOrEditProduct, Authentication authentication) {
        return ApiResponse.success(productService.addProduct(createOrEditProduct, authentication.getName()));
    }

    // Get product by ID
    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable("id") Long productId) {
        return ApiResponse.success(productService.getProductById(productId));
    }

    // Get all products
    @GetMapping("/all")
    public ApiResponse<List<ProductResponse>> getAllProducts() {
        return ApiResponse.success(productService.getAllProducts());
    }

    // Delete product by ID
    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteProductById(@PathVariable("id") Long productId, Authentication authentication) {
        return ApiResponse.success(productService.deleteProductById(productId, authentication.getName()));
    }

    // Update product by ID
    @PutMapping("/edit/{id}")
    public ApiResponse<ProductResponse> updateProductById(@PathVariable("id") Long productId,
                                                           @RequestBody CreateOrEditProduct createOrEditProduct,
                                                           Authentication authentication) {
        return ApiResponse.success(productService.updateProductById(productId, createOrEditProduct, authentication.getName()));
    }
}
