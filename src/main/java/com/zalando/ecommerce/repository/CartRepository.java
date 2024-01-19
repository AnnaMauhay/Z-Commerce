package com.zalando.ecommerce.repository;

import com.zalando.ecommerce.model.CartItem;
import com.zalando.ecommerce.model.Product;
import com.zalando.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> getCartByCustomerAndProduct(User user, Product product);
    List<CartItem> getCartsByCustomer(User user);
}
