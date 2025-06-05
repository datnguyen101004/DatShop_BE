package com.dat.backend.datshop.production.service;

import com.dat.backend.datshop.production.dto.CreateProduct;
import com.dat.backend.datshop.production.dto.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse addProduct(CreateProduct createProduct, String email);

    ProductResponse getProductById(Long id);

    List<ProductResponse> getAllProducts();
}
