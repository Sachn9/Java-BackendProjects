package com.instamart.shopping_delivery.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
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
    int totalPrice;
    String status;
    @ManyToOne
    AppUser deliveryPartner;

    @OneToMany
    List<Product> products;

    @ManyToOne
    AppUser shopper;
    String paymentType;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
