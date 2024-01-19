package com.zalando.ecommerce.dto;

import com.zalando.ecommerce.model.CartItem;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter @Setter
@ToString
public class CartResponse {
    private String message;
    private List<CartItem> cart;
    private Float totalPrice;

    public CartResponse(String message, List<CartItem> cart) {
        this.message=message;
        this.cart = cart;
        this.totalPrice = cart.stream()
                .reduce(0f,(subTotal, cartItem) -> subTotal+cartItem.getQuantity()*cartItem.getProduct().getPrice(), Float::sum);
    }
}
