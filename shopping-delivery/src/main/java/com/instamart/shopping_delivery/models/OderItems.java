package com.instamart.shopping_delivery.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name="orderitems")
public class OderItems {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;
    UUID oderId;
    UUID productId;
    int quantity;
    int totalAmount;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
