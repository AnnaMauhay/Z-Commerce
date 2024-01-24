package com.zalando.ecommerce.repository;

import com.zalando.ecommerce.model.Order;
import com.zalando.ecommerce.model.OrderStatus;
import com.zalando.ecommerce.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<Order> getOrderByCustomerAndOrderId(User user, Integer orderId);
    Page<Order> getOrdersByCustomerAndStatus(User user, OrderStatus status, PageRequest request);
}
