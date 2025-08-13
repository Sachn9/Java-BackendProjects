package com.instamart.shopping_delivery.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class OrderProductDTO {
    UUID orderId;
    String productName;
    int productId;
    int quantity;
    String status;
    String ownerCompany;
    String description;
    String paymentType;
}


