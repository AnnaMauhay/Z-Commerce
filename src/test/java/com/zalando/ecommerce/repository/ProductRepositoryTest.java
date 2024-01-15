package com.zalando.ecommerce.repository;

import com.zalando.ecommerce.model.Product;
import com.zalando.ecommerce.model.Role;
import com.zalando.ecommerce.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    UserRepository userRepository;
    User dummySeller;

    @BeforeEach
    void setUp() {
        dummySeller = new User(1, "John", "Smith", "test@email.com", "password", Role.SELLER, false, false);
        userRepository.save(dummySeller);
    }

    @Test
    void testGetAllProductsBySeller_returnList() {
        Product product1 = new Product("Soap", "Mild soap for delicate skin. 200mL", 2.25f, 10, dummySeller);
        Product product2 = new Product("Shampoo", "Shampoo for colored hair. 100mL", 3.0f, 5, dummySeller);
        productRepository.save(product1);
        productRepository.save(product2);

        List<Product> productList = productRepository.getAllBySellerAndArchivedIsFalse(dummySeller);

        assertFalse(productList.isEmpty());
        assertEquals(2, productList.size());
    }

    @Test
    void testGetAllProductsByDifferentSeller_returnEmptyList() {
        User dummySeller2 = new User(2, "Jane", "Smith", "seller@email.com", "password", Role.SELLER, false, false);
        userRepository.save(dummySeller2);

        Product product1 = new Product("Soap", "Mild soap for delicate skin. 200mL", 2.25f, 10, dummySeller);
        Product product2 = new Product("Shampoo", "Shampoo for colored hair. 100mL", 3.0f, 5, dummySeller);
        productRepository.save(product1);
        productRepository.save(product2);

        List<Product> productList = productRepository.getAllBySellerAndArchivedIsFalse(dummySeller2);

        assertTrue(productList.isEmpty());
    }

    @Test
    void testGetProductsContainingName_returnProduct() {
        List<Product> productList1 = productRepository.getProductsByArchivedIsFalseAndProductNameContainingIgnoreCase("S");
        int count1 = productList1.size();

        List<Product> productList2 = productRepository.getProductsByArchivedIsFalseAndProductNameContainingIgnoreCase("So");
        int count2 = productList2.size();

        List<Product> productList3 = productRepository.getProductsByArchivedIsFalseAndProductNameContainingIgnoreCase("POO");
        int count3 = productList3.size();

        Product product1 = new Product("Soap", "Mild soap for delicate skin. 200mL", 2.25f, 10, dummySeller);
        Product product2 = new Product("Shampoo", "Shampoo for colored hair. 100mL", 3.0f, 5, dummySeller);
        productRepository.save(product1);
        productRepository.save(product2);

        productList1 = productRepository.getProductsByArchivedIsFalseAndProductNameContainingIgnoreCase("S");

        assertFalse(productList1.isEmpty());
        assertEquals(count1+2, productList1.size());


        productList2 = productRepository.getProductsByArchivedIsFalseAndProductNameContainingIgnoreCase("So");
        assertFalse(productList2.isEmpty());
        assertEquals(count2+1, productList2.size());


        productList3 = productRepository.getProductsByArchivedIsFalseAndProductNameContainingIgnoreCase("POO");
        assertFalse(productList3.isEmpty());
        assertEquals(count3+1, productList3.size());
    }

    @Test
    void testGetProductsNotContainingName_returnEmptyList() {
        Product product1 = new Product("Soap", "Mild soap for delicate skin. 200mL", 2.25f, 10, dummySeller);
        Product product2 = new Product("Shampoo", "Shampoo for colored hair. 100mL", 3.0f, 5, dummySeller);
        productRepository.save(product1);
        productRepository.save(product2);

        List<Product> productList = productRepository.getProductsByArchivedIsFalseAndProductNameContainingIgnoreCase("kajsdflkjasd");
        assertTrue(productList.isEmpty());
    }

    @Test
    void testSaveProduct_returnProductWithIdAndDefaultAttributeValues() {
        Product product1 = new Product("Soap", "Mild soap for delicate skin. 200mL", 2.25f, 10, dummySeller);
        Product foundProduct = productRepository.save(product1);

        assertNotEquals(0, foundProduct.getProductId());
        assertFalse(foundProduct.isArchived());
    }
}