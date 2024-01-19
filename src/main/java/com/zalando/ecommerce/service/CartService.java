package com.zalando.ecommerce.service;

import com.zalando.ecommerce.dto.CartRequest;
import com.zalando.ecommerce.dto.CartResponse;
import com.zalando.ecommerce.exception.DuplicateProductException;
import com.zalando.ecommerce.exception.InsufficientStockException;
import com.zalando.ecommerce.exception.ProductNotFoundException;
import com.zalando.ecommerce.exception.StockLimitExceededException;
import com.zalando.ecommerce.model.CartItem;
import com.zalando.ecommerce.model.Product;
import com.zalando.ecommerce.model.User;
import com.zalando.ecommerce.repository.CartRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ProductService productService;

    @Transactional
    public CartResponse addProductToCart(CartRequest cartRequest, User user) throws InsufficientStockException, ProductNotFoundException, DuplicateProductException {
        int quantity = cartRequest.getQuantity();
        Product product = productService.getProductById(cartRequest.getProductId());

        Optional<CartItem> foundCart = cartRepository.getCartByCustomerAndProduct(user, product);
        if (foundCart.isPresent()) {
            throw new DuplicateProductException("The user's cart already contains the same product.");
        } else {
            if (product.getStockQuantity() >= cartRequest.getQuantity()) {
                cartRepository.save(new CartItem(quantity, product, user));
            } else throw new InsufficientStockException("There is not enough stock for the requested quantity.", product);
        }

        return getCartAndTotal(user);
    }

    public CartResponse getCartAndTotal(User user) {
        return new CartResponse("Your cart, " + user.getFirstName(), cartRepository.getCartsByCustomer(user));
    }

    @Transactional
    public CartResponse releaseProductFromCart(int productId, User user) throws ProductNotFoundException {
        Product product = productService.getProductById(productId);

        Optional<CartItem> foundCart = cartRepository.getCartByCustomerAndProduct(user, product);
        if (foundCart.isPresent()) {
            CartItem cartItem = foundCart.get();
            cartRepository.delete(cartItem);
        } else throw new ProductNotFoundException("This user's cart does not contain the provided product ID.");

        return getCartAndTotal(user);
    }

    @Transactional
    public CartResponse updateProductInCart(CartRequest cartRequest, User user) throws ProductNotFoundException, StockLimitExceededException, InsufficientStockException {
        int newQuantity = cartRequest.getQuantity();
        Product product = productService.getProductById(cartRequest.getProductId());
        Optional<CartItem> foundCart = cartRepository.getCartByCustomerAndProduct(user, product);
        if (foundCart.isEmpty()) {
            throw new ProductNotFoundException("This user's cart does not contain the provided product ID.");
        }

        CartItem cartItem = foundCart.get();
        if (product.getStockQuantity() >= newQuantity) {
            cartItem.setQuantity(newQuantity);
        } else throw new InsufficientStockException("There is not enough stock for the requested quantity.", product);

        cartRepository.save(cartItem);
        return getCartAndTotal(user);
    }

    public Set<CartItem> getCartByCustomer(User user) {
        return new HashSet<>(cartRepository.getCartsByCustomer(user));
    }

    public void deletePurchasedCartItems(User user) {
        List<CartItem> cartItemList = cartRepository.getCartsByCustomer(user);
        cartRepository.deleteAll(cartItemList);
    }
}
