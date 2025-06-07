package com.dat.backend.datshop.cart.service.impl;

import com.dat.backend.datshop.cart.dto.AddProduct;
import com.dat.backend.datshop.cart.dto.CartResponse;
import com.dat.backend.datshop.cart.entity.Cart;
import com.dat.backend.datshop.cart.entity.CartItem;
import com.dat.backend.datshop.cart.repository.CartItemRepository;
import com.dat.backend.datshop.cart.repository.CartRepository;
import com.dat.backend.datshop.cart.service.CartService;
import com.dat.backend.datshop.production.entity.Product;
import com.dat.backend.datshop.production.mapper.ProductMapper;
import com.dat.backend.datshop.production.repository.ProductRepository;
import com.dat.backend.datshop.user.entity.User;
import com.dat.backend.datshop.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public String addProductToCart(AddProduct addProduct, String email) {
        // Fetch user
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Check if cart exists for user, if not create one
        Optional<Cart> cart_op = cartRepository.findByUserId(user.getId());
        if (cart_op.isEmpty()) {
            log.info("Creating new cart for user: {}", user.getEmail());
            Cart newCart = new Cart();
            newCart.setUserId(user.getId());
            cartRepository.save(newCart);
        }

        Cart cart = cart_op.get();

        // Add product to cart_item table
        CartItem cartItem = new CartItem();
        cartItem.setCartId(cart.getId());
        cartItem.setProductId(addProduct.getProductId());
        cartItem.setQuantity(addProduct.getQuantity());
        cartItemRepository.save(cartItem);
        log.info("Product with ID: {} added to cart for user: {}", addProduct.getProductId(), user.getEmail());

        return "Added product to cart successfully";
    }

    @Override
    public CartResponse getCart(String email) {
        // Fetch user
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        // Fetch cart for user
        Optional<Cart> cart_op = cartRepository.findByUserId(user.getId());
        if (cart_op.isEmpty()) {
            log.info("Creating new cart for user: {}", user.getEmail());
            Cart newCart = new Cart();
            newCart.setUserId(user.getId());
            cartRepository.save(newCart);
        }
        Cart cart = cart_op.get();

        // Fetch cart items
        List<CartItem> cartItem = cartItemRepository.findByCartId(cart.getId());

        // Get all products in cart item
        if (cartItem.isEmpty()) {
            return null;
        }
        List<Product> products = new ArrayList<>();
        for (CartItem item : cartItem) {
            Long productId = item.getProductId();
            // Fetch product by ID
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
            products.add(product);
        }

        // return CartResponse
        CartResponse cartResponse = new CartResponse();
        cartResponse.setCartId(cart.getId());
        cartResponse.setUserId(cart.getUserId());
        cartResponse.setProducts(products.stream().map(productMapper::productToProductResponse).toList());

        return cartResponse;
    }
}
