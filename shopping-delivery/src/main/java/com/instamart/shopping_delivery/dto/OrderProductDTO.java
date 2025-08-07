package com.instamart.shopping_delivery.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class OrderProductDTO {
    UUID productId;
    String productName;
    int quantity;
    String ownerCompany;
    String description;
}


