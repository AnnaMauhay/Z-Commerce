package com.zalando.ecommerce.repository;

import com.zalando.ecommerce.model.Product;
import com.zalando.ecommerce.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    /*
     *  CUSTOMER-Role-related Queries
     */
    Optional<Product> findProductsByProductIdAndArchivedIsFalse(Integer productId);
    Page<Product> findAllByArchivedIsFalse(Pageable pageable);
    List<Product> getProductsByArchivedIsFalseAndProductNameContainingIgnoreCase(String productName);
    Page<Product> getProductsByArchivedIsFalseAndProductNameContainingIgnoreCase(String productName, Pageable pageable);

    /*
     *  SELLER-Role-related Queries
     */
    Optional<Product> getProductByProductNameContainingIgnoreCaseAndSeller(String productName, User user);
    Optional<Product> getProductByProductIdAndSellerAndArchived(Integer productId, User user, Boolean archived);
    Product getProductByProductIdAndSeller(Integer productId, User seller);
    List<Product> getAllBySellerAndArchivedIsFalse(User user);
}
