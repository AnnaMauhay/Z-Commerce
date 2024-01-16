package com.zalando.ecommerce.dto;

import com.zalando.ecommerce.model.Cart;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class CartResponse {
    private List<Cart> cartList;
    private Float totalPrice;

    public CartResponse(List<Cart> cartList) {
        this.cartList = cartList;
        this.totalPrice = cartList.stream()
                .reduce(0f,(subTotal, cart) -> subTotal+cart.getQty()*cart.getProduct().getPrice(), Float::sum);
    }
}
