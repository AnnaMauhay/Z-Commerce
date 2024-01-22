package com.zalando.ecommerce.dto;

import com.zalando.ecommerce.model.CartItem;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Getter @Setter
@ToString
public class CartResponse {
    private String message;
    private List<CartItem> cart;
    private BigDecimal totalPrice;

    public CartResponse(String message, List<CartItem> cart) {
        this.message=message;
        this.cart = cart;
        BigDecimal price = cart.stream()
                .reduce(BigDecimal.ZERO,(subTotal, cartItem) -> subTotal.add(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))), BigDecimal::add);
        this.totalPrice = price.setScale(3, RoundingMode.HALF_UP);
    }
}
