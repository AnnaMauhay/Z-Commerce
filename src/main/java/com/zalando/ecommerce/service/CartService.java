package com.zalando.ecommerce.service;

import com.zalando.ecommerce.dto.CartRequest;
import com.zalando.ecommerce.dto.CartResponse;
import com.zalando.ecommerce.exception.InsufficientStockException;
import com.zalando.ecommerce.exception.ProductNotFoundException;
import com.zalando.ecommerce.exception.StockLimitExceededException;
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
    public CartResponse addProductToCart(CartRequest cartRequest, User user) throws InsufficientStockException, ProductNotFoundException {
        int quantity = cartRequest.getQty();

        Product product = productService.getProductById(cartRequest.getProductId());
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

    public CartResponse getCartAndTotal(User user) {
        return new CartResponse("Your cart, " + user.getFirstName(), cartRepository.getCartsByCustomer(user));
    }

    @Transactional
    public CartResponse removeProductFromCart(int productId, User user) throws ProductNotFoundException, StockLimitExceededException {
        Product product = productService.getProductById(productId);

        Optional<Cart> foundCart = cartRepository.getCartByCustomerAndProduct(user, product);
        if (foundCart.isPresent()) {
            Cart cart = foundCart.get();
            productService.increaseQty(cart.getQty(), product);
            cartRepository.delete(cart);
        } else throw new ProductNotFoundException("This user's cart does not contain the provided product ID.");

        return getCartAndTotal(user);
    }

    @Transactional
    public CartResponse reduceProductInCart(CartRequest cartRequest, User user) throws ProductNotFoundException, StockLimitExceededException, InsufficientStockException {
        int quantity = cartRequest.getQty();

        Product product = productService.getProductById(cartRequest.getProductId());
        Optional<Cart> foundCart = cartRepository.getCartByCustomerAndProduct(user, product);
        if (foundCart.isPresent()) {
            Cart cart = foundCart.get();
            productService.increaseQty(quantity, product);
            if (cart.reduceQty(quantity)){
                cartRepository.save(cart);
                return getCartAndTotal(user);
            } throw new InsufficientStockException("The quantity in the cart is less than the requested number to be removed.");
        } else throw new ProductNotFoundException("This user's cart does not contain the provided product ID.");
    }
}
