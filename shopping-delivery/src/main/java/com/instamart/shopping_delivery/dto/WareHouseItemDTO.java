package com.instamart.shopping_delivery.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class WareHouseItemDTO {

    UUID productId;
    UUID wid;
    String productName;
    double price;
    int quantity;
    boolean isAvailable;
    double discount;
}
