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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * This class is an integration test for the ProductController class.
 * This activates the 'test' and 'h2' profiles.
 * Sample data is populated through the SampleProductLoader class (which is a CommandLineRunner).
 * SampleProductLoader is run before TestDispatcherServlet is initialised.
 *
 * @Author Anna Liza Mauhay
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = {"test", "h2"})
class ProductControllerTest {
    @Autowired
    private WebApplicationContext applicationContext;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
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

    @SneakyThrows
    @Test
    @WithMockUser(username = "seller1@email.com", roles = {"SELLER"})
    void givenAuthorizedUser_testAddNewProduct_returnCreated() {
        assertThat(getPrincipal()).isInstanceOfAny(org.springframework.security.core.userdetails.User.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "productName": "All new Bacon",
                                    "description": "American style smoked bacon",
                                    "price": 6.5,
                                    "stockQty": 10
                                }
                                """))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").isNumber())
                .andExpect(jsonPath("$.productName").value("All new Bacon"))
                .andExpect(redirectedUrlPattern("**/products/*"));
    }

    @SneakyThrows
    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void givenUnAuthorizedUserRole_testAddNewProduct_returnForbidden() {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "productName": "All new Bacon",
                                    "description": "American style smoked bacon",
                                    "price": 6.5,
                                    "stockQty": 10
                                }
                                """))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "seller1@email.com", roles = {"SELLER"})
    void givenExistingProduct_testAddNewProduct_returnNotAcceptable() {
        Optional<User> seller = userRepository.getUserByEmail("seller1@email.com");
        if (seller.isPresent()) {
            productRepository.save(Product.builder()
                    .productName("Smoked Salmon")
                    .description("Smoked salmon with herbs")
                    .price(BigDecimal.valueOf(6.5f))
                    .stockQuantity(10)
                    .seller(seller.get())
                    .build());

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/products")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "productName": "Smoked Salmon",
                                        "description": "Smoked salmon with herbs",
                                        "price": 6.5,
                                        "stockQty": 10
                                    }
                                    """))
                    .andDo(print())
                    .andExpect(status().isNotAcceptable());
        }
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "seller1@email.com", roles = {"SELLER"})
    void givenExistingProduct_testUpdateProduct_returnOk() {
        Optional<User> seller = userRepository.getUserByEmail("seller1@email.com");
        Product existingProduct;
        if (seller.isPresent()) {
            existingProduct = productRepository.save(Product.builder()
                    .productName("Existing Product")
                    .description("This is an existing product")
                    .price(BigDecimal.valueOf(6.5f))
                    .stockQuantity(10)
                    .seller(seller.get())
                    .build());

            mockMvc.perform(MockMvcRequestBuilders
                            .put("/products/" + existingProduct.getProductId())
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "productName": "Updated Product",
                                        "description": "This is an updated product",
                                        "price": 6.5,
                                        "stockQty": 10
                                    }
                                    """))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.productName").value("Updated Product"))
                    .andExpect(jsonPath("$.description").value("This is an updated product"));
        }
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "seller1@email.com", roles = {"SELLER"})
    void givenNonExistingProduct_testUpdateProduct_returnNotAcceptable() {
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/products/" + 0)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "productName": "Updated Product",
                                    "description": "This is an updated product",
                                    "price": 6.5,
                                    "stockQty": 10
                                }
                                """))
                .andDo(print())
                .andExpect(status().isNotAcceptable());
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "seller1@email.com", roles = {"SELLER"})
    void givenExistingProduct_testDeleteProduct_returnOk() {
        Optional<User> seller = userRepository.getUserByEmail("seller1@email.com");
        Product existingProduct;
        if (seller.isPresent()) {
            existingProduct = productRepository.save(Product.builder()
                    .productName("Existing Product 2")
                    .description("This is an existing product 2")
                    .price(BigDecimal.valueOf(6.5f))
                    .stockQuantity(10)
                    .seller(seller.get())
                    .build());

            mockMvc.perform(MockMvcRequestBuilders
                            .delete("/products/" + existingProduct.getProductId())
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.archived").value(true));
        }
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "seller1@email.com", roles = {"SELLER"})
    void givenNonExistingProduct_testDeleteProduct_returnNotFound() {
        Optional<User> seller = userRepository.getUserByEmail("seller1@email.com");
        Product existingProduct;
        if (seller.isPresent()) {
            existingProduct = productRepository.save(Product.builder()
                    .productName("Existing Product 3")
                    .description("This is an existing product 3")
                    .price(BigDecimal.valueOf(6.5f))
                    .stockQuantity(10)
                    .seller(seller.get())
                    .archived(true)
                    .build());

            mockMvc.perform(MockMvcRequestBuilders
                            .delete("/products/" + existingProduct.getProductId())
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotAcceptable());
        }
    }

    private Object getPrincipal() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}