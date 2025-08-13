package com.instamart.shopping_delivery.models;

import jakarta.persistence.*;
import lombok.Data;
//import lombok.Generated;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Table(name="orderitems")
public class OderItems {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;
    
    @ManyToOne
    AppOrder order;

    @ManyToOne
    Product product;

    int quantity;
    BigDecimal unitPrice;
    BigDecimal lineTotal;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
