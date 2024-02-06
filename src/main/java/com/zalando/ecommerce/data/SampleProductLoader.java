package com.zalando.ecommerce.data;

import com.github.javafaker.Faker;
import com.zalando.ecommerce.model.Product;
import com.zalando.ecommerce.model.Role;
import com.zalando.ecommerce.model.User;
import com.zalando.ecommerce.repository.ProductRepository;
import com.zalando.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
@Profile("test")
public class SampleProductLoader implements CommandLineRunner {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final Faker faker;
    private final Logger logger = LoggerFactory.getLogger(SampleProductLoader.class);

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            logger.info("Creating sample users...");
            User seller1 = User.builder()
                    .firstName("Seller1")
                    .lastName("Lastname")
                    .email("seller1@email.com")
                    .password("testPass1")
                    .role(Role.SELLER)
                    .build();
            User buyer1 = User.builder()
                    .firstName("Buyer1")
                    .lastName("Lastname")
                    .email("buyer1@email.com")
                    .password("testPass1")
                    .role(Role.CUSTOMER)
                    .build();
            User buyer2 = User.builder()
                    .firstName("Buyer2")
                    .lastName("Lastname")
                    .email("buyer2@email.com")
                    .password("testPass1")
                    .role(Role.CUSTOMER)
                    .build();
            userRepository.saveAll(List.of(seller1, buyer1, buyer2));
        }

        Random random = new Random();
        logger.info("Creating sample products...");

        Optional<User> foundUser = userRepository.getUserByEmail("seller1@email.com");
        if (foundUser.isPresent() && productRepository.count() == 0) {
            User user = foundUser.get();

            List<Product> productList = IntStream.range(0, 50)
                    .mapToObj(product -> Product.builder()
                            .productName(faker.commerce().productName())
                            .description(faker.commerce().color())
                            .price(BigDecimal.valueOf(random.nextFloat(0.990f, 5.950f)))
                            .stockQuantity(random.nextInt(30, 50))
                            .seller(user)
                            .build())
                    .toList();

            productRepository.saveAll(productList);
        } else logger.info("Sample data cannot be created. User was not found in the database.");
    }
}
