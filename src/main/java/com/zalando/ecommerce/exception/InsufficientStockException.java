package com.zalando.ecommerce.exception;

import com.zalando.ecommerce.model.CartItem;
import com.zalando.ecommerce.model.Product;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
@Getter
public class InsufficientStockException extends Exception {
    private final Set<Product> unavailableProducts = new HashSet<>();
    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException() {
        super();
    }

    public InsufficientStockException(String message, Set<CartItem> unavailableCartItemItems) {
        super(message);
        unavailableCartItemItems.forEach(cart -> unavailableProducts.add(cart.getProduct()));
    }

    public InsufficientStockException(String message, Product product) {
        super(message);
        unavailableProducts.add(product);
    }
}
