package com.zalando.ecommerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusHistoryKey implements Serializable {
    @Column(name = "modifier_id")
    private int modifierId;

    @Column(name = "order_id")
    private int orderId;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof OrderStatusHistoryKey that)) return false;
        return getModifierId() == that.getModifierId() && getOrderId() == that.getOrderId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getModifierId(), getOrderId());
    }
}
