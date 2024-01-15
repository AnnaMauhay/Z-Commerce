package com.zalando.ecommerce.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.assertThat;

class ProductRequestTest {
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();
    private Set<ConstraintViolation<ProductRequest>> violations;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        productRequest = new ProductRequest();
    }

    @Test
    void setValidProductRequest_returnNoError() {
        productRequest.setProductName("Product Name");
        productRequest.setDescription("Description");
        productRequest.setPrice(0f);
        productRequest.setStockQty(10);

        violations = validator.validate(productRequest);
        assertEquals(0, violations.size());
    }

    @Test
    void setInvalidProductName_returnValidationError() {
        productRequest.setDescription("Description");
        productRequest.setPrice(1f);
        productRequest.setStockQty(10);

        productRequest.setProductName("");

        violations = validator.validate(productRequest);
        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting("message")
                .containsOnly("Product name cannot be empty.");

        productRequest.setProductName(null);

        violations = validator.validate(productRequest);
        assertEquals(2, violations.size());
        assertThat(violations)
                .extracting("message")
                .contains("Product name cannot be empty.")
                .contains("Product name cannot be null.");
    }

    @Test
    void setInvalidDescription_returnValidationError() {
        productRequest.setProductName("Product Name");
        productRequest.setPrice(1f);
        productRequest.setStockQty(10);

        productRequest.setDescription("");

        violations = validator.validate(productRequest);
        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting("message")
                .containsOnly("Product description cannot be empty.");

        productRequest.setDescription(null);

        violations = validator.validate(productRequest);
        assertEquals(2, violations.size());
        assertThat(violations)
                .extracting("message")
                .contains("Product description cannot be empty.")
                .contains("Product description cannot be null.");
    }

    @Test
    void setNegativePrice_returnValidationError() {
        productRequest.setProductName("Product Name");
        productRequest.setDescription("Description");
        productRequest.setStockQty(10);

        productRequest.setPrice(-1f);

        violations = validator.validate(productRequest);
        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting("message")
                .containsOnly("Price must be a positive number.");
    }

    @Test
    void setVeryBigPrice_returnValidationError() {
        productRequest.setProductName("Product Name");
        productRequest.setDescription("Description");
        productRequest.setStockQty(10);

        productRequest.setPrice(1000001f);

        violations = validator.validate(productRequest);
        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting("message")
                .contains("Price must not be more than 1,000,000.");
    }

    @Test
    void setNegativeStockQty_returnValidationError() {
        productRequest.setProductName("Product Name");
        productRequest.setDescription("Description");
        productRequest.setPrice(1f);

        productRequest.setStockQty(-1);

        violations = validator.validate(productRequest);
        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting("message")
                .containsOnly("Stock quantity must be a positive number.");
    }

    @Test
    void setVeryBigStockQty_returnValidationError() {
        productRequest.setProductName("Product Name");
        productRequest.setDescription("Description");
        productRequest.setPrice(1f);

        productRequest.setStockQty(10001);

        violations = validator.validate(productRequest);
        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting("message")
                .containsOnly("Stock quantity must not be more than 10,000.");
    }
}