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
    @JsonIgnore
    @EmbeddedId
    private OrderStatusHistoryKey id;

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
    @MapsId("modifierId")
    @JoinColumn(name = "modifier_id")
    private User modifier;

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    private Order order;

    public OrderStatusHistory(OrderStatus oldStatus, OrderStatus newStatus,
                              User modifier, Order order) {
        this.id = new OrderStatusHistoryKey(modifier.getUserId(), order.getOrderId());
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.dateModified = LocalDateTime.now();
        this.order = order;
        this.modifier = modifier;
    }
}
