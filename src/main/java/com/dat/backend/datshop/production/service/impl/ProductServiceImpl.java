package com.dat.backend.datshop.production.service.impl;

import com.dat.backend.datshop.production.dto.CreateProduct;
import com.dat.backend.datshop.production.dto.ProductResponse;
import com.dat.backend.datshop.production.entity.Product;
import com.dat.backend.datshop.production.mapper.ProductMapper;
import com.dat.backend.datshop.production.repository.ProductRepository;
import com.dat.backend.datshop.production.service.ProductService;
import com.dat.backend.datshop.user.entity.User;
import com.dat.backend.datshop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final UserRepository userRepository;

    @Override
    public ProductResponse addProduct(CreateProduct createProduct, String email) {
        log.info("Adding product: {}", createProduct.getName());
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        Product product = new Product();
        product = productMapper.createProductToProduct(createProduct);
        product.setAuthor(user);
        productRepository.save(product);
        log.info("Product added successfully: {}", product.getName());
        log.info("Author: {}", user.getEmail());
        ProductResponse productResponse = productMapper.productToProductResponse(product);
        productResponse.setAuthor(email);
        return productResponse;
    }

    @Override
    public ProductResponse getProductById(Long id) {
        log.info("Fetching product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        log.info("Product found: {}", product.getName());
        return productMapper.productToProductResponse(product);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        log.info("Fetching all products");
        List<Product> products = productRepository.findAll();
        if (!products.isEmpty()) {
            log.info("Found {} products", products.size());
            List<ProductResponse> productResponses = new ArrayList<>();
            for (Product product : products) {
                productResponses.add(productMapper.productToProductResponse(product));
            }
            return productResponses;
        }
        log.info("No products found");
        return new ArrayList<>();
    }
}
