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
    private int quantity;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @JsonIgnore
    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    private Order purchaseOrder;

    public OrderItem(int quantity, Product product, Order order) {
        this.id=new OrderItemKey(product.getProductId(), order.getOrderId());
        this.quantity = quantity;
        this.product = product;
        this.purchaseOrder=order;
    }
}
