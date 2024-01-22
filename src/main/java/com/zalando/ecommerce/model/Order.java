package com.zalando.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
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
    @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern="0.###")
    private BigDecimal totalPrice;

    @Column @Enumerated(value = EnumType.ORDINAL)
    private OrderStatus status;

    @Column(name = "is_archived")
    private boolean archived;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    public Order(User customer, Set<CartItem> cartItemSet, OrderStatus status) {
        this.date = LocalDateTime.now();
        this.customer = customer;
        BigDecimal price = cartItemSet.stream()
                .reduce(BigDecimal.ZERO.setScale(3),
                        (subTotal, cartItem) -> subTotal.add(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))),
                        BigDecimal::add);
        this.totalPrice = price.setScale(3, RoundingMode.HALF_UP);
        this.status = status;
    }
}
