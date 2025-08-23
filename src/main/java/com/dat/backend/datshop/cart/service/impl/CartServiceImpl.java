package com.dat.backend.datshop.cart.service.impl;

import com.dat.backend.datshop.cart.dto.ProductItemRequest;
import com.dat.backend.datshop.cart.dto.CartItemResponse;
import com.dat.backend.datshop.cart.dto.UpdateCartProduct;
import com.dat.backend.datshop.cart.entity.Cart;
import com.dat.backend.datshop.cart.entity.CartItem;
import com.dat.backend.datshop.cart.mapper.CartItemMapper;
import com.dat.backend.datshop.cart.repository.CartItemRepository;
import com.dat.backend.datshop.cart.repository.CartRepository;
import com.dat.backend.datshop.cart.service.CartService;
import com.dat.backend.datshop.product.entity.Product;
import com.dat.backend.datshop.product.repository.ProductRepository;
import com.dat.backend.datshop.user.entity.User;
import com.dat.backend.datshop.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    @Transactional
    public String addProductToCart(ProductItemRequest productItemRequest, String email) {
        // Get the cart of the user
        Cart cart = checkCart(email);

        // Check if the stock is enough
        Product product = productRepository.findById(productItemRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("Information not found with ID: " + productItemRequest.getProductId()));
        if (product.getStockQuantity() < productItemRequest.getQuantity()) {
            log.warn("Information has enough stock to add product to the cart");
            throw new RuntimeException("Not enough stock for product ID: " + productItemRequest.getProductId());
        }

        // Check if product exists increase quantity else create new cart item
        Optional<CartItem> cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productItemRequest.getProductId());
        if (cartItem.isPresent()) {
            // If product already exists in cart, increase quantity
            CartItem existingItem = cartItem.get();
            existingItem.setQuantity(existingItem.getQuantity() + productItemRequest.getQuantity());
            cartItemRepository.save(existingItem);
            log.info("Increased quantity of product ID {} in cart ID {}", productItemRequest.getProductId(), cart.getId());
        } else {
            // If product does not exist in cart, create new cart item
            CartItem newCartItem = new CartItem();
            newCartItem.setCartId(cart.getId());
            newCartItem.setProductId(productItemRequest.getProductId());
            newCartItem.setQuantity(productItemRequest.getQuantity());
            cartItemRepository.save(newCartItem);
            log.info("Added product ID {} to cart ID {}", productItemRequest.getProductId(), cart.getId());
        }

        return "Added product to cart successfully";
    }

    @Override
    public List<CartItemResponse> getCart(String email) {
        // Create if not exists or get the cart of the user
        Cart cart = checkCart(email);

        // Fetch cart items
        List<CartItem> cartItem = cartItemRepository.findByCartId(cart.getId());

        // Get all products in cart item
        if (cartItem.isEmpty()) {
            return null;
        }

        return cartItem.stream().map(cartItemMapper::mapToCartItemResponse).collect(Collectors.toList());
    }

    @Override
    public String removeProductFromCart(Long id, String name) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + name));

        // Get the cart of the user
        Cart cart = checkCart(name);

        // Check if product exists in cart or the quantity is possible to remove
        Optional<CartItem> cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), id);
        if (cartItem.isPresent()) {
            CartItem existingItem = cartItem.get();
            cartItemRepository.delete(existingItem);
            log.info("Removed product ID {} from cart ID {}", id, cart.getId());
            return "Removed product from cart successfully";
        }

        // Nếu không tìm thấy sản phẩm trong giỏ hàng, ném ngoại lệ
        log.warn("Information ID {} not found in cart ID {}", id, cart.getId());
        throw new RuntimeException("Information not found in cart");
    }

    @Override
    @Transactional
    public String updateCartItemQuantity(UpdateCartProduct updateCartProduct, String name) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + name));

        // Get the cart of the user
        Cart cart = checkCart(name);

        // Cập nhật số lượng sản phẩm trong giỏ hàng
        for (ProductItemRequest productItemRequest : updateCartProduct.getItems()) {
            if (productItemRequest.getQuantity() == 0) {
                // Nếu số lượng là 0, xóa sản phẩm khỏi giỏ hàng
                removeProductFromCart(productItemRequest.getProductId(), name);
            } else {
                // Kiểm tra xem sản phẩm có trong giỏ hàng không
                Optional<CartItem> cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productItemRequest.getProductId());
                if (cartItem.isPresent()) {
                    CartItem existingItem = cartItem.get();
                    // Cập nhật số lượng sản phẩm
                    existingItem.setQuantity(productItemRequest.getQuantity());
                    cartItemRepository.save(existingItem);
                    log.info("Updated quantity of product ID {} in cart ID {}", productItemRequest.getProductId(), cart.getId());
                } else {
                    log.warn("Product ID {} not found in cart ID {}", productItemRequest.getProductId(), cart.getId());
                    throw new RuntimeException("Product not found in cart");
                }
            }
        }

        return "Updated cart item quantities successfully";
    }

    @Override
    public String clearCart(String name) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + name));

        // Get the cart of the user
        Cart cart = checkCart(name);

        // Clear all items in the cart
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (!cartItems.isEmpty()) {
            cartItemRepository.deleteAll(cartItems);
            log.info("Cleared all items in cart ID {}", cart.getId());
            return "Cleared cart successfully";
        }
        log.warn("No items found in cart ID {}", cart.getId());
        throw new RuntimeException("No items found in cart");
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
