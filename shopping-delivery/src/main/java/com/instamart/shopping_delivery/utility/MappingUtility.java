package com.instamart.shopping_delivery.utility;

import com.instamart.shopping_delivery.dto.WareHouseRegistrationDTO;
import com.instamart.shopping_delivery.models.Location;
import com.instamart.shopping_delivery.models.WareHouse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MappingUtility {

    public WareHouse wareHouseToDTOModel(WareHouseRegistrationDTO wareHouseRegistrationDTO,
                                         Location location){

        //System.out.println("DTO Name: " + wareHouseRegistrationDTO.getWareHouseName());

        WareHouse wareHouse=new WareHouse();
        wareHouse.setName(wareHouseRegistrationDTO.getWareHouseName());
        wareHouse.setLocation(location);
        wareHouse.setCreatedAt(LocalDateTime.now());
        wareHouse.setUpdatedAt(LocalDateTime.now());
        return  wareHouse;
    }
}
