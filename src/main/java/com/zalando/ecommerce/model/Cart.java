package com.zalando.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "cart")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @JsonIgnore
    @EmbeddedId
    private CartKey id;

    @Column
    private int qty;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @JsonIgnore
    @ManyToOne
    @MapsId("customerId")
    @JoinColumn(name = "customer_id")
    private User customer;

    public Cart(int qty, Product product, User customer) {
        this.id=new CartKey(product.getProductId(), customer.getUserId());
        this.qty = qty;
        this.product = product;
        this.customer = customer;
    }

    public int addQty(int qty){
        this.qty+=qty;
        return qty;
    }
}
