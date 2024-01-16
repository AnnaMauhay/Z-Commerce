package com.zalando.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "product_order")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @JsonIgnore
    @EmbeddedId
    private OrderItemKey id;

    @Column
    private int qty;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @JsonIgnore
    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    private Order order;

    public OrderItem(int qty, Product product, Order order) {
        this.id=new OrderItemKey(product.getProductId(), order.getOrderId());
        this.qty = qty;
        this.product = product;
    }
}
