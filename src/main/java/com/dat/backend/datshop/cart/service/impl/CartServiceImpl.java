package com.dat.backend.datshop.cart.service.impl;

import com.dat.backend.datshop.cart.dto.AddOrRemoveProduct;
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
    public String addProductToCart(AddOrRemoveProduct addOrRemoveProduct, String email) {
        // Get the cart of the user
        Cart cart = checkCart(email);

        // Check if the stock is enough
        Product product = productRepository.findById(addOrRemoveProduct.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + addOrRemoveProduct.getProductId()));
        if (product.getStockQuantity() < addOrRemoveProduct.getQuantity()) {
            log.warn("Product has enough stock to add product to the cart");
            throw new RuntimeException("Not enough stock for product ID: " + addOrRemoveProduct.getProductId());
        }
        else {
            log.info("Product has enough stock to add product to the cart");
            // Reduce stock quantity
            product.setStockQuantity(product.getStockQuantity() - addOrRemoveProduct.getQuantity());
            productRepository.save(product);
        }

        // Check if product exists increase quantity else create new cart item
        Optional<CartItem> cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), addOrRemoveProduct.getProductId());
        if (cartItem.isPresent()) {
            // If product already exists in cart, increase quantity
            CartItem existingItem = cartItem.get();
            existingItem.setQuantity(existingItem.getQuantity() + addOrRemoveProduct.getQuantity());
            cartItemRepository.save(existingItem);
            log.info("Increased quantity of product ID {} in cart ID {}", addOrRemoveProduct.getProductId(), cart.getId());
        } else {
            // If product does not exist in cart, create new cart item
            CartItem newCartItem = new CartItem();
            newCartItem.setCartId(cart.getId());
            newCartItem.setProductId(addOrRemoveProduct.getProductId());
            newCartItem.setQuantity(addOrRemoveProduct.getQuantity());
            cartItemRepository.save(newCartItem);
            log.info("Added product ID {} to cart ID {}", addOrRemoveProduct.getProductId(), cart.getId());
        }

        return "Added product to cart successfully";
    }

    @Override
    public CartResponse getCart(String email) {
        // Get the cart of the user
        Cart cart = checkCart(email);

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

    @Override
    public String removeProductFromCart(AddOrRemoveProduct addOrRemoveProduct, String name) {
        // Get the cart of the user
        Cart cart = checkCart(name);

        // Check if product exists in cart or the quantity is possible to remove
        Optional<CartItem> cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), addOrRemoveProduct.getProductId());
        if (cartItem.isPresent()) {
            CartItem existingItem = cartItem.get();
            if (existingItem.getQuantity() < addOrRemoveProduct.getQuantity()) {
                log.warn("Not enough quantity to remove from cart");
                throw new RuntimeException("Not enough quantity to remove from cart");
            } else if (existingItem.getQuantity() == addOrRemoveProduct.getQuantity()) {
                // If quantity is equal, remove the item from cart
                cartItemRepository.delete(existingItem);
                log.info("Removed product ID {} from cart ID {}", addOrRemoveProduct.getProductId(), cart.getId());
            } else {
                // If quantity is more than requested, decrease the quantity
                existingItem.setQuantity(existingItem.getQuantity() - addOrRemoveProduct.getQuantity());
                cartItemRepository.save(existingItem);
                log.info("Decreased quantity of product ID {} in cart ID {}", addOrRemoveProduct.getProductId(), cart.getId());
            }
            return "Removed product from cart successfully";
        }

        log.warn("Product ID {} not found in cart ID {}", addOrRemoveProduct.getProductId(), cart.getId());
        throw new RuntimeException("Product not found in cart");
    }

    // Check if cart exists for user, if not create one
    private Cart checkCart(String email) {
        // Fetch user
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Check if cart exists for user, if not create one
        Optional<Cart> cart_op = cartRepository.findByUserId(user.getId());
        if (cart_op.isEmpty()) {
            log.info("Creating new cart for user: {}", user.getEmail());
            Cart newCart = new Cart();
            newCart.setUserId(user.getId());
            cartRepository.save(newCart);
            return newCart;
        }

        return cart_op.get();
    }
}
