package com.dat.backend.datshop.production.controller;

import com.dat.backend.datshop.production.dto.CreateProduct;
import com.dat.backend.datshop.production.dto.ProductResponse;
import com.dat.backend.datshop.production.entity.Product;
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
    public ApiResponse<ProductResponse> addProduct(@RequestBody CreateProduct createProduct, Authentication authentication) {
        return ApiResponse.success(productService.addProduct(createProduct, authentication.getName()));
    }

    // Get product by ID
    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable Long id) {
        return ApiResponse.success(productService.getProductById(id));
    }

    // Get all products
    @GetMapping("/all")
    public ApiResponse<List<ProductResponse>> getAllProducts() {
        return ApiResponse.success(productService.getAllProducts());
    }
}
