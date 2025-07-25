package com.instamart.shopping_delivery.dto;

import com.instamart.shopping_delivery.models.Location;
import lombok.Data;

@Data
public class WareHouseRegistrationDTO {
    String wareHouseName;
    Location location;
}
