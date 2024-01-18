package com.zalando.ecommerce.repository;

import com.zalando.ecommerce.model.Order;
import com.zalando.ecommerce.model.OrderStatus;
import com.zalando.ecommerce.model.Role;
import com.zalando.ecommerce.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
    @BeforeEach
    void setUp() {
        dummyCustomer = new User(1, "John", "Smith", "test@email.com", "password", Role.CUSTOMER, false, false);
        userRepository.save(dummyCustomer);
        float totalPrice = 20.3f;
        order = new Order(dummyCustomer,totalPrice, OrderStatus.PROCESSING);
    }

    @Test
    public void testSaveNewOrder_returnOrderWithIdAndDefaultAttributeValues(){
        Order savedOrder = orderRepository.save(order);
        assertNotEquals(0, savedOrder.getOrderId());
        assertThat(savedOrder.getDate()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.MINUTES));
        assertEquals(OrderStatus.PROCESSING, savedOrder.getStatus());
        assertFalse(savedOrder.isArchived());
    }
}