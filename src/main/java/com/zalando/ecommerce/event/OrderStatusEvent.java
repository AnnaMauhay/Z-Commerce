package com.zalando.ecommerce.event;

import com.zalando.ecommerce.model.OrderStatusHistory;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
@Getter
public class OrderStatusEvent extends ApplicationEvent {
    private final OrderStatusHistory orderStatusHistory;
    public OrderStatusEvent(Object source, OrderStatusHistory orderStatusHistory) {
        super(source);
        this.orderStatusHistory = orderStatusHistory;
    }
}
