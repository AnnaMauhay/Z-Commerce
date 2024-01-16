package com.zalando.ecommerce.repository;

import com.zalando.ecommerce.model.Cart;
import com.zalando.ecommerce.model.Product;
import com.zalando.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> getCartByCustomerAndProduct(User user, Product product);
}
