package com.zalando.ecommerce.repository;

import com.zalando.ecommerce.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;
    private User dummyCustomer;
    private Order order;
    private float unitPrice;
    private int quantity;
    private Product product;
    @BeforeEach
    void setUp() {
        User dummySeller = new User(1, "Jane", "Smith", "test.seller@email.com", "password", Role.SELLER, false, false);
        dummyCustomer = new User(2, "John", "Smith", "test.buyer@email.com", "password", Role.CUSTOMER, false, false);

        userRepository.save(dummySeller);
        userRepository.save(dummyCustomer);

        unitPrice = 2.25f;
        quantity = 10;

        product = new Product("Soap", "Mild soap for delicate skin. 200mL", unitPrice, 10, dummySeller);
        productRepository.save(product);

        order = new Order(dummyCustomer, Set.of(new CartItem(quantity,product, dummyCustomer)), OrderStatus.PROCESSING);
    }

    @Test
    public void testSaveNewOrder_returnOrderWithIdAndDefaultAttributeValues(){
        Order savedOrder = orderRepository.save(order);
        assertNotEquals(0, savedOrder.getOrderId());
        assertThat(savedOrder.getDate()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.MINUTES));
        assertEquals(OrderStatus.PROCESSING, savedOrder.getStatus());
        assertFalse(savedOrder.isArchived());
        assertEquals(unitPrice * quantity, savedOrder.getTotalPrice());
    }
}