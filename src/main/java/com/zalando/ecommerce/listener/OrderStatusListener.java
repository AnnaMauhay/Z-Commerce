package com.zalando.ecommerce.listener;

import com.zalando.ecommerce.model.OrderStatusHistory;
import com.zalando.ecommerce.model.OrderStatusNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusListener implements ApplicationListener<OrderStatusNotification> {
    private final Logger logger = LoggerFactory.getLogger(OrderStatusListener.class);
    @Override
    public void onApplicationEvent(OrderStatusNotification notification) {
        OrderStatusHistory history = notification.getOrderStatusHistory();
        logger.info("Status of order " + history.getOrder().getOrderId() +
                " has been changed from '" + history.getOldStatus() +
                "' to '" + history.getNewStatus() +
                "' by user: " + history.getModifier().getEmail());
    }
}
