package com.zalando.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "cart")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
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

    public Cart(int quantity, Product product, User customer) {
        this.id=new CartKey(product.getProductId(), customer.getUserId());
        this.quantity = quantity;
        this.product = product;
        this.customer = customer;
    }

    public void addQuantity(int quantity){
        this.quantity +=quantity;
    }

    public boolean reduceQuantity(int quantity) {
        if (this.quantity < quantity) {
            return false;
        } else {
            this.quantity -= quantity;
            return true;
        }
    }
}
