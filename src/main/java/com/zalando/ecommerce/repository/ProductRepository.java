package com.zalando.ecommerce.repository;

import com.zalando.ecommerce.model.Product;
import com.zalando.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> getAllBySeller(User user);
    List<Product> getProductsByProductNameContainingIgnoreCase(String productName);
}
