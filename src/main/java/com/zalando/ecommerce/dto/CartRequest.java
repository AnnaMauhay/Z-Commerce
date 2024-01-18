package com.zalando.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartRequest {
    @NotEmpty(message="Product ID cannot be empty.")
    @NotNull (message="Product ID cannot be null.")
    private int productId;
    @NotEmpty (message="Quantity cannot be empty.")
    @NotNull(message="Quantity cannot be null.")
    @Min(value = 1, message = "Quantity cannot be less than 1.")
    private int quantity;
}
