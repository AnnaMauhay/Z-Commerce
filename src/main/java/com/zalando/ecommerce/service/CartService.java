package com.zalando.ecommerce.service;

import com.zalando.ecommerce.dto.CartResponse;
import com.zalando.ecommerce.exception.InsufficientStockException;
import com.zalando.ecommerce.exception.ProductNotFoundException;
import com.zalando.ecommerce.model.Cart;
import com.zalando.ecommerce.model.Product;
import com.zalando.ecommerce.model.User;
import com.zalando.ecommerce.repository.CartRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ProductService productService;

    @Transactional
    public CartResponse addProductToCart(int quantity, int productId, User user) throws InsufficientStockException, ProductNotFoundException {
        Product product = productService.getProductById(productId);
        productService.reduceQuantity(quantity, product);

        Optional<Cart> foundCart = cartRepository.getCartByCustomerAndProduct(user, product);
        if (foundCart.isPresent()) {
            Cart cart = foundCart.get();
            cart.addQty(quantity);
            cartRepository.save(cart);
        } else {
            cartRepository.save(new Cart(quantity, product, user));
        }

        return getCartAndTotal(user);
    }

    public CartResponse getCartAndTotal(User user){
        return new CartResponse(cartRepository.getCartsByCustomer(user));
    }
}
