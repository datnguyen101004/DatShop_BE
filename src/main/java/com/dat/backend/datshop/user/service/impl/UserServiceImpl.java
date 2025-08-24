package com.dat.backend.datshop.user.service.impl;

import com.dat.backend.datshop.product.entity.Product;
import com.dat.backend.datshop.product.mapper.ProductMapper;
import com.dat.backend.datshop.product.repository.ProductRepository;
import com.dat.backend.datshop.user.dto.HomeResponse;
import com.dat.backend.datshop.user.dto.UserResponse;
import com.dat.backend.datshop.user.entity.User;
import com.dat.backend.datshop.user.mapper.UserMapper;
import com.dat.backend.datshop.user.repository.UserRepository;
import com.dat.backend.datshop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public UserResponse getProfile(String email) {
        // Get user by ID
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getUserProfile(Long id, String name) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id + " and name: " + name));

        return userMapper.toUserResponse(user);
    }

    @Override
    public HomeResponse getHome() {
        List<String> categories = productRepository.findDistinctCategories();
        List<Product> products = findTop4Products();
        return HomeResponse.builder()
                .categories(categories)
                .products(products.stream().map(productMapper::productToProductResponse).toList())
                .build();
    }

    private List<Product> findTop4Products() {
        return productRepository.findTop4ByOrderByIdDesc();
    }
}
