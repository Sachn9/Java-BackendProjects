package com.instamart.shopping_delivery.controller;

import com.instamart.shopping_delivery.dto.WareHouseRegistrationDTO;
import com.instamart.shopping_delivery.models.WareHouse;
import com.instamart.shopping_delivery.service.WareHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warehouse")
public class WareHouseController{

    WareHouseService wareHouseService;

    @Autowired
    public WareHouseController(WareHouseService wareHouseService){
        this.wareHouseService=wareHouseService;
    }

    @PostMapping("/registration")
    public ResponseEntity registrationWareHouse(@RequestParam UUID userId,
                                                @RequestBody WareHouseRegistrationDTO wareHouseRegistrationDTO){

        //We are getting userId from requestParam And we are getting wareHouseRegistrationDTO in request body.
        //Now we should call wareHouse Service to implement the logic.
        WareHouse wareHouse= wareHouseService.wareHouseRegistration(userId,wareHouseRegistrationDTO);
        return new ResponseEntity(wareHouse, HttpStatus.CREATED);


    }


}
