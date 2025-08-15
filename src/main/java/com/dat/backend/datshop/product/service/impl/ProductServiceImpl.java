package com.dat.backend.datshop.product.service.impl;

import com.dat.backend.datshop.product.dto.ActionToProduct;
import com.dat.backend.datshop.product.dto.ProductResponse;
import com.dat.backend.datshop.product.entity.Product;
import com.dat.backend.datshop.product.mapper.ProductMapper;
import com.dat.backend.datshop.product.repository.ProductRepository;
import com.dat.backend.datshop.product.service.ProductService;
import com.dat.backend.datshop.user.entity.User;
import com.dat.backend.datshop.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ProductResponse addProduct(ActionToProduct actionToProduct, String email) {
        log.info("Adding product: {}", actionToProduct.getName());

        // fetch user by email
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Add product to product table
        Product product = productMapper.createProductToProduct(actionToProduct);
        product.setAuthor(user);
        productRepository.save(product);

        // Log product addition
        log.info("Information added successfully: {}", product.getName());
        log.info("Author: {}", user.getEmail());
        return productMapper.productToProductResponse(product);
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        log.info("Fetching product with ID: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Information not found with ID: " + productId));
        log.info("Information found: {}", product.getName());
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

    @Override
    @Transactional
    public String deleteProductById(Long productId, String email) {
        // fetch user and product
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Information not found with ID: " + productId));

        // Check authorization
        if (!Objects.equals(product.getAuthor(), user)) {
            log.warn("User {} is not authorized to delete product with ID: {}", email, productId);
            throw new RuntimeException("You are not authorized to delete this product");
        }

        // if authorized, delete product in product table and user product table
        productRepository.deleteById(productId);
        return "Deleted product with ID: " + productId + " successfully";
    }

    @Override
    @Transactional
    public ProductResponse updateProductById(Long productId, ActionToProduct actionToProduct, String email) {
        // fetch user and product
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Information not found with ID: " + productId));

        // Check authorization
        if (!Objects.equals(product.getAuthor(), user)) {
            log.warn("User {} is not authorized to update product with ID: {}", email, productId);
            throw new RuntimeException("You are not authorized to update this product");
        }

        // Update product details
        product.setName(actionToProduct.getName());
        product.setDescription(actionToProduct.getDescription());
        product.setPrice(actionToProduct.getPrice());
        product.setImageUrl(actionToProduct.getImageUrl());
        product.setCategory(actionToProduct.getCategory());
        productRepository.save(product);
        log.info("Information with ID: {} updated successfully by user: {}", productId, email);

        return productMapper.productToProductResponse(product);
    }
}
