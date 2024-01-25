package com.zalando.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_status_history")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusHistory {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private int historyId;

    @Column(name = "old_status")
    @Enumerated
    private OrderStatus oldStatus;

    @Column(name = "new_status")
    @Enumerated
    private OrderStatus newStatus;

    @Column(name = "date_modified")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dateModified;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "modifier_id")
    private User modifier;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    public OrderStatusHistory(OrderStatus oldStatus, OrderStatus newStatus,
                              User modifier, Order order) {
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.dateModified = LocalDateTime.now();
        this.order = order;
        this.modifier = modifier;
    }
}
