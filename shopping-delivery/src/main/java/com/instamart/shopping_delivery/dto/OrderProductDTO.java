package com.instamart.shopping_delivery.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class OrderProductDTO {
    private UUID productId;
    private int quantity;
    private String paymentType;
}
