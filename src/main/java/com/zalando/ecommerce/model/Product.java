package com.zalando.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class Product {
    @JsonIgnore
    @Transient
    private final int MAX_QUANTITY = 10_000;

    @Id @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int productId;

    @Column(name = "product_name")
    private String productName;

    @Column
    private String description;

    @Column
    private BigDecimal price;

    @Column(name = "stock_quantity")
    private int stockQuantity;

    @Column(name = "is_archived")
    private boolean archived;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    public Product(String productName, String description, Float floatPrice, int stockQuantity, User seller) {
        this.productName = productName;
        this.description = description;
        this.price = BigDecimal.valueOf(floatPrice).setScale(3, RoundingMode.HALF_UP);
        this.stockQuantity = stockQuantity;
        this.seller = seller;
    }

    public boolean reduceQuantity(int quantity) {
        if (this.stockQuantity < quantity){
            return false;
        }else{
            this.stockQuantity -= quantity;
            return true;
        }
    }

    public boolean increaseQuantity(int quantity){
        if (this.stockQuantity + quantity > MAX_QUANTITY) return false;

        this.stockQuantity += quantity;
        return true;
    }
}
