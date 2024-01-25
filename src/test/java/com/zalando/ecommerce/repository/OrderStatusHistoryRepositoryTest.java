package com.zalando.ecommerce.repository;

import com.zalando.ecommerce.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderStatusHistoryRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Autowired
    UserRepository userRepository;
    private User dummyAdmin;
    private User dummyCustomer;
    private Order order;
    private String adminEmail;
    @BeforeEach
    void setUp() {
        adminEmail = "test.admin@email.com";
        User dummySeller = new User(1, "Jane", "Smith", "test.seller@email.com", "password", Role.SELLER, false, false);
        dummyCustomer = new User(2, "John", "Smith", "test.buyer@email.com", "password", Role.CUSTOMER, false, false);
        dummyAdmin = new User(3, "Admin", "Smith", adminEmail, "password", Role.ADMIN, false, false);

        userRepository.save(dummySeller);
        userRepository.save(dummyCustomer);
        userRepository.save(dummyAdmin);

        Product product = new Product("Soap", "Mild soap for delicate skin. 200mL", 2.25f, 1000, dummySeller);
        order = new Order(dummyCustomer, Set.of(new CartItem(10,product, dummyCustomer)), OrderStatus.PROCESSING);
        orderRepository.save(order);
    }

    @Test
    void givenNewOrderStatusHistory_testSave(){
        OrderStatus oldStatus = OrderStatus.PROCESSING;
        OrderStatus newStatus = OrderStatus.SHIPPED;

        OrderStatusHistory history = orderStatusHistoryRepository.save(new OrderStatusHistory(oldStatus, newStatus, dummyAdmin, order));
        assertNotEquals(0, history.getHistoryId());
        assertThat(history.getDateModified()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.MINUTES));
        assertEquals(OrderStatus.PROCESSING, history.getOldStatus());
        assertEquals(OrderStatus.SHIPPED, history.getNewStatus());
        assertEquals(adminEmail, history.getModifier().getEmail());
        assertEquals(BigDecimal.valueOf(22.5).setScale(3), history.getOrder().getTotalPrice());
    }
}