package com.zalando.ecommerce.controller;

import com.zalando.ecommerce.model.Product;
import com.zalando.ecommerce.model.User;
import com.zalando.ecommerce.repository.ProductRepository;
import com.zalando.ecommerce.repository.UserRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = {"test", "h2"})
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductController productController;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(productController)
                .build();
    }

    @SneakyThrows
    @Test
    void testGetAllProducts_returnOk() {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empty").value(false));
    }

    @SneakyThrows
    @Test
    void givenExistingKeyword_testGetProductsWithName_returnProduct() {
        Optional<User> foundUser = userRepository.getUserByEmail("seller1@email.com");
        if (foundUser.isPresent()) {
            productRepository.save(Product.builder()
                    .productName("Gentle Soap")
                    .description("Gentle for delicate skin.")
                    .price(BigDecimal.valueOf(2.5f))
                    .stockQuantity(20)
                    .seller(foundUser.get())
                    .build());
        } else fail("User was not found in the database.");

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/products/search")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("keyword", "Gentle Soap")
                        .param("page", String.valueOf(0))
                        .param("size", String.valueOf(5)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empty").value(false));
    }

    @SneakyThrows
    @Test
    void givenNonExistingKeyword_testGetProductsWithName_returnEmpty() {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/products/search")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("keyword", "dfhgjkhafd")
                        .param("page", String.valueOf(0))
                        .param("size", String.valueOf(5)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empty").value(true));
    }

    @SneakyThrows
    @Test
    void givenExistingProductId_testGetProductById_returnProduct() {
        Optional<User> foundUser = userRepository.getUserByEmail("seller1@email.com");
        Product product = null;
        if (foundUser.isPresent()) {
            product = productRepository.save(Product.builder()
                    .productName("Baby Shampoo")
                    .description("Gentle for babies.")
                    .price(BigDecimal.valueOf(2.5f))
                    .stockQuantity(20)
                    .seller(foundUser.get())
                    .build());
        } else fail("User was not found in the database.");

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/products/" + product.getProductId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").isNotEmpty());
    }

    @SneakyThrows
    @Test
    void givenNonExistingProductId_testGetProductById_returnNotFound() {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/products/" + 0)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}