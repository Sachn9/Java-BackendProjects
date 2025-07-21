package com.instamart.shopping_delivery.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name="warehouses")
public class WareHouseItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;
    UUID wid;
    UUID pid;
    int quantity;
    int discount;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
