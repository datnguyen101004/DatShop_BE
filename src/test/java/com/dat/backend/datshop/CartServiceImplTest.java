package com.dat.backend.datshop;

import com.dat.backend.datshop.cart.entity.Cart;
import com.dat.backend.datshop.cart.mapper.CartItemMapper;
import com.dat.backend.datshop.cart.repository.CartItemRepository;
import com.dat.backend.datshop.cart.repository.CartRepository;
import com.dat.backend.datshop.cart.service.impl.CartServiceImpl;
import com.dat.backend.datshop.product.repository.ProductRepository;
import com.dat.backend.datshop.user.entity.User;
import com.dat.backend.datshop.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CartItemMapper cartItemMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    @Test
    void returnsEmptyListWhenCartHasNoItems() {
        User user = User.builder().id(1L).email("cart-test@datshop.local").build();
        Cart cart = Cart.builder().id(10L).userId(user.getId()).build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartId(cart.getId())).thenReturn(List.of());

        var result = cartService.getCart(user.getEmail());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
