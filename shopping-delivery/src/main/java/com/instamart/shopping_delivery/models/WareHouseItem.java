package com.instamart.shopping_delivery.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name="warehouseitems")
public class WareHouseItem{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;
    UUID wid;
    UUID pid;
    @OneToMany
    List<Product> productList;
    int quantity;
    int discount;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
