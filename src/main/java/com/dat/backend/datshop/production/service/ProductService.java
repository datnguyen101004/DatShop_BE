package com.dat.backend.datshop.production.service;

import com.dat.backend.datshop.production.dto.CreateOrEditProduct;
import com.dat.backend.datshop.production.dto.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse addProduct(CreateOrEditProduct createOrEditProduct, String email);

    ProductResponse getProductById(Long productId);

    List<ProductResponse> getAllProducts();

    String deleteProductById(Long productId, String email);

    ProductResponse updateProductById(Long productId, CreateOrEditProduct createOrEditProduct, String email);
}
