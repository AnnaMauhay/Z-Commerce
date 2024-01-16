package com.zalando.ecommerce.service;

import com.zalando.ecommerce.model.Cart;
import com.zalando.ecommerce.model.Product;
import com.zalando.ecommerce.model.User;
import com.zalando.ecommerce.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;

    public List<Cart> addProductToCart(int quantity, Product product, User user) {
        Cart cart;
        Optional<Cart> foundCart = cartRepository.getCartByCustomerAndProduct(user, product);
        if (foundCart.isPresent()) {
            cart = foundCart.get();
            if (product.getStockQty() >= quantity) {
                cart.addQty(quantity);
            }
        } else {
            cartRepository.save(new Cart(quantity, product, user));
        }
        return null;
    }
}
