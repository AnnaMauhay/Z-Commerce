package com.zalando.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Setter @Getter @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "product")
public class Product {
    @Id @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int productId;
    @Column(name = "product_name")
    private String productName;
    @Column
    private String description;
    @Column
    private Float price;
    @Column
    private int stockQty;

    @Column(name = "is_archived")
    private boolean isArchived;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    public Product(String productName, String description, Float price, int stockQty, User seller) {
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.stockQty = stockQty;
        this.seller = seller;
    }
}
