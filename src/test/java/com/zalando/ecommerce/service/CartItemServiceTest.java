package com.zalando.ecommerce.service;

import com.zalando.ecommerce.dto.CartRequest;
import com.zalando.ecommerce.dto.CartResponse;
import com.zalando.ecommerce.exception.DuplicateProductException;
import com.zalando.ecommerce.exception.ProductNotFoundException;
import com.zalando.ecommerce.model.CartItem;
import com.zalando.ecommerce.model.Product;
import com.zalando.ecommerce.model.Role;
import com.zalando.ecommerce.model.User;
import com.zalando.ecommerce.repository.CartRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {
    @Mock
    private CartRepository cartRepository;
    @Mock
    private ProductService productService;

    @InjectMocks
    private CartService cartService;

    @Captor
    ArgumentCaptor<CartItem> cartCaptor;

    private CartItem cartItem;
    private CartRequest cartRequest;
    private User user;
    private int quantity;
    private float price;
    private int productId;
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
        cartItem = new CartItem(quantity, product, user);
    }

    @SneakyThrows
    @Test
    void givenNewProduct_whenAddProductToCart_thenReturnCartResponse() {
        cartRequest = new CartRequest(productId, quantity);
        given(productService.getProductById(productId)).willReturn(product);
        given(cartRepository.getCartsByCustomer(user)).willReturn(List.of(cartItem));

        CartResponse cartResponse;
        cartResponse = cartService.addProductToCart(cartRequest, user);

        System.out.println(cartResponse);
        assertThat(cartResponse).isNotNull();
        assertThat(cartResponse.getCart()).isNotEmpty();
        assertEquals(quantity * price, cartResponse.getTotalPrice());
    }

    @SneakyThrows
    @Test
    void givenExistingProduct_whenAddProductToCart_throwDuplicateProductException() {
        cartRequest = new CartRequest(productId, quantity);
        given(productService.getProductById(productId)).willReturn(product);
        given(cartRepository.getCartByCustomerAndProduct(user, product)).willReturn(Optional.of(cartItem));

        assertThrows(DuplicateProductException.class, () ->
                cartService.addProductToCart(cartRequest, user));
    }

    @SneakyThrows
    @Test
    void givenProductInCart_whenRemoveProductFromCart_thenCartIsEmpty() {
        given(productService.getProductById(productId)).willReturn(product);
        given(cartRepository.getCartByCustomerAndProduct(user, product)).willReturn(Optional.of(cartItem));

        CartResponse cartResponse;
        cartResponse = cartService.releaseProductFromCart(productId, user);

        System.out.println(cartResponse);
        assertThat(cartResponse).isNotNull();
        assertThat(cartResponse.getCart()).isEmpty();
        assertEquals(0f, cartResponse.getTotalPrice());
    }

    @SneakyThrows
    @Test
    void givenProductNotPresentInCart_whenRemoveProductFromCart_throwProductNotFoundException() {
        given(productService.getProductById(productId)).willReturn(product);
        given(cartRepository.getCartByCustomerAndProduct(user, product)).willReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () ->
                cartService.releaseProductFromCart(productId, user));
    }

    @SneakyThrows
    @Test
    void testUpdateProductInCartToHigherQuantity_thenNewTotalPriceIncreases() {
        cartRequest = new CartRequest(productId, quantity + 10);
        given(productService.getProductById(productId)).willReturn(product);
        given(cartRepository.getCartByCustomerAndProduct(user, product)).willReturn(Optional.of(cartItem));

        cartService.updateProductInCart(cartRequest, user);

        verify(cartRepository).save(cartCaptor.capture());
        CartItem cartItemCaptorValue = cartCaptor.getValue();
        assertEquals(quantity+10, cartItemCaptorValue.getQuantity());
    }

    @SneakyThrows
    @Test
    void testUpdateProductInCartToLowerQuantity_thenNewTotalPriceDecreases() {
        cartRequest = new CartRequest(productId, quantity - 5);
        given(productService.getProductById(productId)).willReturn(product);
        given(cartRepository.getCartByCustomerAndProduct(user, product)).willReturn(Optional.of(cartItem));

        cartService.updateProductInCart(cartRequest, user);

        verify(cartRepository).save(cartCaptor.capture());
        CartItem cartItemCaptorValue = cartCaptor.getValue();
        assertEquals(quantity-5, cartItemCaptorValue.getQuantity());
    }
}