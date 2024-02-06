package com.zalando.ecommerce.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ProductRequest {
    @NotNull(message = "Product name cannot be null.")
    @NotEmpty(message = "Product name cannot be empty.")
    private String productName;

    @NotNull(message = "Product description cannot be null.")
    @NotEmpty(message = "Product description cannot be empty.")
    private String description;

    @DecimalMin(value = "0", inclusive = true, message = "Price must be a positive number.")
    @DecimalMax(value = "1000000", inclusive = true, message = "Price must not be more than 1,000,000.")
    @Digits(integer=7, fraction=3)
    private float price;

    @NotNull(message = "Product price cannot be null.")
    @Min(value = 0, message = "Stock quantity must be a positive number.")
    @Max(value = 10_000,message = "Stock quantity must not be more than 10,000.")
    private int stockQty;
}
