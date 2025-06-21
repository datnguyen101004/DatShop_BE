package com.dat.backend.datshop.product.service;

import com.dat.backend.datshop.product.dto.ActionToProduct;
import com.dat.backend.datshop.product.dto.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse addProduct(ActionToProduct actionToProduct, String email);

    ProductResponse getProductById(Long productId);

    List<ProductResponse> getAllProducts();

    String deleteProductById(Long productId, String email);

    ProductResponse updateProductById(Long productId, ActionToProduct actionToProduct, String email);

}
