package com.zalando.ecommerce.dto;

import com.zalando.ecommerce.model.OrderStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderUpdateRequest {
    @NotNull(message = "Order status cannot be null.")
    private OrderStatus status;
}
