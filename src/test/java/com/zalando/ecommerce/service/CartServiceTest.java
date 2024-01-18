package com.zalando.ecommerce.service;

import com.zalando.ecommerce.dto.CartRequest;
import com.zalando.ecommerce.dto.CartResponse;
import com.zalando.ecommerce.exception.DuplicateProductException;
import com.zalando.ecommerce.exception.InsufficientStockException;
import com.zalando.ecommerce.exception.ProductNotFoundException;
import com.zalando.ecommerce.model.Cart;
import com.zalando.ecommerce.model.Product;
import com.zalando.ecommerce.model.Role;
import com.zalando.ecommerce.model.User;
import com.zalando.ecommerce.repository.CartRepository;
import com.zalando.ecommerce.repository.ProductRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
    @Mock
    private CartRepository cartRepository;
    @Mock
    private ProductService productService;

    @InjectMocks
    private CartService cartService;

    private Cart cart;
    private CartRequest cartRequest;
    private User user;
    private int quantity;
    private float price;
    private int productId;
    @InjectMocks
    private Product product;

    @BeforeEach
    void setUp() {
        quantity = 10;
        price = 4f;
        productId = 1;

        user = new User(1, "Customer1", "CustomerLastName", "customer@email.com",
                "password", Role.CUSTOMER, false, false);

        product = new Product(productId, "Sample Product", "Product Description",
                price, 50, false, user);
        cart = new Cart(quantity, product, user);
    }

    //    @SneakyThrows
    @SneakyThrows
    @Test
    void givenNewProduct_whenAddProductToCart_thenReturnCartResponse() {
        cartRequest = new CartRequest(productId, quantity);
        given(productService.getProductById(productId)).willReturn(product);
        given(cartRepository.getCartsByCustomer(user)).willReturn(List.of(cart));

        CartResponse cartResponse;
        cartResponse = cartService.addProductToCart(cartRequest, user);

        System.out.println(cartResponse);
        assertThat(cartResponse).isNotNull();
        assertThat(cartResponse.getCartList()).isNotEmpty();
        assertEquals(quantity * price, cartResponse.getTotalPrice());
    }

    @SneakyThrows
    @Test
    void givenExistingProduct_whenAddProductToCart_throwDuplicateProductException() {
        cartRequest = new CartRequest(productId, quantity);
        given(productService.getProductById(productId)).willReturn(product);
        given(cartRepository.getCartByCustomerAndProduct(user, product)).willReturn(Optional.of(cart));

        assertThrows(DuplicateProductException.class, () ->
                cartService.addProductToCart(cartRequest, user));
    }

    @SneakyThrows
    @Test
    void givenProductInCart_whenRemoveProductFromCart_thenCartIsEmpty() {
        given(productService.getProductById(productId)).willReturn(product);
        given(cartRepository.getCartByCustomerAndProduct(user, product)).willReturn(Optional.of(cart));

        CartResponse cartResponse;
        cartResponse = cartService.removeProductFromCart(productId, user);

        System.out.println(cartResponse);
        assertThat(cartResponse).isNotNull();
        assertThat(cartResponse.getCartList()).isEmpty();
        assertEquals(0f, cartResponse.getTotalPrice());
    }

    @SneakyThrows
    @Test
    void givenProductNotPresentInCart_whenRemoveProductFromCart_throwProductNotFoundException() {
        given(productService.getProductById(productId)).willReturn(product);
        given(cartRepository.getCartByCustomerAndProduct(user, product)).willReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () ->
                cartService.removeProductFromCart(productId, user));
    }

    @SneakyThrows
    @Test
    void testUpdateProductInCartToHigherQuantity_thenNewTotalPriceIncreases() {
        cartRequest = new CartRequest(productId, quantity + 10);
        given(productService.getProductById(productId)).willReturn(product);
        given(cartRepository.getCartByCustomerAndProduct(user, product)).willReturn(Optional.of(cart));

        Cart updatedCart = when(cartRepository.save(any(Cart.class))).getMock();
        given(cartRepository.getCartsByCustomer(user)).willReturn(List.of(updatedCart));

//        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
//            Cart updatedCart = invocation.getArgument(0);
//            updatedCart.setQuantity(quantity+10);
//            return updatedCart;
//        });
//        given(cartRepository.getCartsByCustomer(user)).willReturn(List.of(updatedCart));

        CartResponse cartResponse;
        cartResponse = cartService.updateProductInCart(cartRequest, user);

        System.out.println(cartResponse);
        assertThat(cartResponse).isNotNull();
        assertThat(cartResponse.getCartList()).isNotEmpty();
        assertEquals(price*(quantity+10), cartResponse.getTotalPrice());
    }
}