package com.zalando.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "cart")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @JsonIgnore
    @EmbeddedId
    private CartKey id;

    @Column
    private int quantity;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @JsonIgnore
    @ManyToOne
    @MapsId("customerId")
    @JoinColumn(name = "customer_id")
    private User customer;

    public CartItem(int quantity, Product product, User customer) {
        this.id=new CartKey(product.getProductId(), customer.getUserId());
        this.quantity = quantity;
        this.product = product;
        this.customer = customer;
    }
}
