package com.instamart.shopping_delivery.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductDTO {
    UUID id;
    String productName;
    int unitPrice;
    int totalQuantity;
    String ownerCompany;
    String description;
}
