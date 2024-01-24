package com.zalando.ecommerce.model;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
@Getter
public class OrderStatusNotification extends ApplicationEvent {
    private final OrderStatusHistory orderStatusHistory;
    public OrderStatusNotification(Object source, OrderStatusHistory orderStatusHistory) {
        super(source);
        this.orderStatusHistory = orderStatusHistory;
    }
}
