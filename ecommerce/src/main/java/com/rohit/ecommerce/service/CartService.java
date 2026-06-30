package com.rohit.ecommerce.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rohit.ecommerce.dto.AddToCartRequest;
import com.rohit.ecommerce.dto.CartItemResponse;
import com.rohit.ecommerce.dto.CartResponse;
import com.rohit.ecommerce.exception.ResourceNotFoundException;
import com.rohit.ecommerce.model.Cart;
import com.rohit.ecommerce.model.CartItem;
import com.rohit.ecommerce.model.Product;
import com.rohit.ecommerce.model.User;
import com.rohit.ecommerce.repository.CartItemRepository;
import com.rohit.ecommerce.repository.CartRepository;
import com.rohit.ecommerce.repository.ProductRepository;
import com.rohit.ecommerce.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private Cart getOrCreateCart(String email) {
        User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("user not found"));

        return cartRepository.findByUser(user)
        .orElseGet(() -> {
            Cart newCart = Cart.builder().user(user).build();
            return cartRepository.save(newCart);
        });
    }

    @Transactional
    public CartResponse addToCart(String email, AddToCartRequest request) {
        Cart cart = getOrCreateCart(email);

        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product", request.getProductId()));

        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
            .orElse(null);

        if (cartItem == null) {
            cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(request.getQuantity())
                .build();
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        }

        cartItemRepository.save(cartItem);

    
        cart = cartRepository.findById(cart.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        return toResponse(cart);
    }

    public CartResponse getCart(String email) {
        Cart cart = getOrCreateCart(email);  // calls the private helper
        return toResponse(cart);
    }

    @Transactional
    public CartResponse removeFromCart(String email, Long cartItemId) {
        Cart cart = getOrCreateCart(email);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
        .orElseThrow(() -> new ResourceNotFoundException("CartItem", cartItemId));

        cart.getItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
        return toResponse(cart);
    }

    private CartResponse toResponse(Cart cart) {

        List<CartItemResponse> itemResponses = cart.getItems().stream()
        .map(item -> new CartItemResponse(
            item.getId(),
            item.getProduct().getId(),
            item.getProduct().getName(),
            item.getProduct().getPrice(),
            item.getQuantity(),
            item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
        ))
        .toList();

        BigDecimal total = itemResponses.stream()
        .map(CartItemResponse::getSubTotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);


        return new CartResponse(cart.getId(), itemResponses, total);
    }

}
