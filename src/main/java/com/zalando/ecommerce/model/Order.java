package com.zalando.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity @Table(name = "purchase_order")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "order_id")
    private int orderId;

    @Column
    private LocalDateTime date;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @Column(name = "total_price")
    private float totalPrice;

    @Column @Enumerated(value = EnumType.ORDINAL)
    private OrderStatus status;

    @Column(name = "is_archived")
    private boolean archived;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
    private Set<OrderItem> orderItems;

    public Order(User customer, Set<CartItem> cartItemSet, OrderStatus status) {
        this.date = LocalDateTime.now();
        this.customer = customer;
        this.totalPrice = cartItemSet.stream()
                .reduce(0f,
                        (subTotal, cartItem) -> subTotal + cartItem.getQuantity() * cartItem.getProduct().getPrice(),
                        Float::sum);
        this.status = status;
    }
}
