package com.zalando.ecommerce.repository;

import com.zalando.ecommerce.model.Cart;
import com.zalando.ecommerce.model.Product;
import com.zalando.ecommerce.model.Role;
import com.zalando.ecommerce.model.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CartRepositoryTest {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    UserRepository userRepository;
    private User user;
    private Product product1;
    private Product product2;
    @BeforeEach
    void setUp() {
        userRepository.save(new User(
                1,"John", "Smith",
                "test@email.com","password",
                Role.SELLER,false,false));
        Optional<User> foundUser = userRepository.getUserByEmail("test@email.com");
        foundUser.ifPresent(value -> user = value);

        product1 = productRepository.save(new Product("Soap","Mild soap for delicate skin. 200mL",
                2.25f,10,user));
        product2 = productRepository.save(new Product("Shampoo","Shampoo for colored hair. 100mL",
                3.0f,5,user));
    }

    @Test
    void testSaveCartWithExistingUserAndProduct_cartRepoCountIncreases(){
        long previousCount = cartRepository.count();
        cartRepository.save(new Cart(1,productRepository.getReferenceById(product1.getProductId()),user));
        cartRepository.save(new Cart(2,productRepository.getReferenceById(product2.getProductId()),user));
        assertEquals(previousCount+2,cartRepository.count());
    }

    @Test
    void testSaveSameProductAndUser_cartRepoCountNotAffected(){
        long previousCount = cartRepository.count();
        cartRepository.save(new Cart(1,productRepository.getReferenceById(product1.getProductId()),user));
        assertEquals(previousCount+1,cartRepository.count());

        cartRepository.save(new Cart(1,productRepository.getReferenceById(product1.getProductId()),user));
        assertEquals(previousCount+1,cartRepository.count());
    }
}