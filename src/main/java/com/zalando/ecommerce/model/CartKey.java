package com.zalando.ecommerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartKey implements Serializable{
    @Column(name = "product_id")
    private int productId;
    @Column(name = "customer_id")
    private int customerId;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof CartKey cartKey)) return false;
        return getProductId() == cartKey.getProductId() && getCustomerId() == cartKey.getCustomerId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProductId(), getCustomerId());
    }
}
