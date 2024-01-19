package com.zalando.ecommerce.service;

import com.zalando.ecommerce.exception.EmptyCartException;
import com.zalando.ecommerce.exception.InsufficientStockException;
import com.zalando.ecommerce.model.*;
import com.zalando.ecommerce.repository.OrderItemRepository;
import com.zalando.ecommerce.repository.OrderRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private CartService cartService;
    @Mock
    private ProductService productService;
    @InjectMocks
    private OrderService orderService;

    @Captor
    ArgumentCaptor<Order> orderCaptor;

    private User user;
    private Order order;
    private float unitPrice;
    private int quantity;
    private Product product;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        User dummySeller = new User(1, "Jane", "Smith", "test.seller@email.com", "password", Role.SELLER, false, false);
        user = new User(2, "John", "Smith", "test.buyer@email.com", "password", Role.CUSTOMER, false, false);

        unitPrice = 2.25f;
        quantity = 10;

        product = new Product(1, "Soap", "Mild soap for delicate skin. 200mL", unitPrice, 10, false, dummySeller);
        cartItem = new CartItem(quantity, product, user);
        order = new Order(user, Set.of(cartItem), OrderStatus.PROCESSING);
        order.setOrderId(1);
        order.setDate(LocalDateTime.now());
    }

    @SneakyThrows
    @Test
    void givenExistingCart_testCreateOrder_correctTotalPrice() {
        given(cartService.getCartByCustomer(user)).willReturn(Set.of(cartItem));
        given(orderRepository.save(any())).willReturn(order);

        orderService.createOrder(user);

        verify(orderRepository).save(orderCaptor.capture());
        Order orderCaptorValue = orderCaptor.getValue();
        assertEquals(unitPrice * quantity, orderCaptorValue.getTotalPrice());
    }

    @SneakyThrows
    @Test
    void givenEmptyCart_testCreateOrder_throwEmptyCartException() {
        given(cartService.getCartByCustomer(user)).willReturn(new HashSet<>());

        assertThrows(EmptyCartException.class, () -> orderService.createOrder(user));
    }

    @SneakyThrows
    @Test
    void givenProductWithNotEnoughStock_testCreateOrder_throwInsufficientStockException() {
        cartItem.setQuantity(quantity+10);
        given(cartService.getCartByCustomer(user)).willReturn(Set.of(cartItem));

        assertThrows(InsufficientStockException.class, () -> orderService.createOrder(user));
    }
}