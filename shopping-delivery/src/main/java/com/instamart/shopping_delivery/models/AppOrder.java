package com.instamart.shopping_delivery.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name="orders")
public class AppOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;
    int totalItems;
    BigDecimal totalPrice;
    @ManyToOne(fetch = FetchType.LAZY)
    AppUser deliveryPartner;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OderItems> items;

    @ManyToOne(fetch = FetchType.LAZY)
    AppUser shopper;
    String paymentType;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
