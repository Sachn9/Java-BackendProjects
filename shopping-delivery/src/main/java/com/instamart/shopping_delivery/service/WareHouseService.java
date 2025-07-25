package com.instamart.shopping_delivery.service;

import com.instamart.shopping_delivery.dto.WareHouseRegistrationDTO;
import com.instamart.shopping_delivery.exception.InvalidOperationException;
import com.instamart.shopping_delivery.models.AppUser;
import com.instamart.shopping_delivery.models.Location;
import com.instamart.shopping_delivery.models.WareHouse;
import com.instamart.shopping_delivery.repositories.WareHouseRepository;
import com.instamart.shopping_delivery.utility.MappingUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.MappedByteBuffer;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class WareHouseService {

    AppUserService appUserService;
    WareHouseRepository wareHouseRepository;
    LocationService locationService;
    MappingUtility mappingUtility;
    MailService mailService;
    @Autowired
    public WareHouseService(AppUserService appUserService,
                            WareHouseRepository wareHouseRepository,
                            LocationService locationService,
                            MappingUtility mappingUtility,
                            MailService mailService){
        this.appUserService=appUserService;
        this.wareHouseRepository=wareHouseRepository;
        this.locationService=locationService;
        this.mappingUtility=mappingUtility;
        this.mailService=mailService;

    }


    public WareHouse saveWareHouse(WareHouse wareHouse){
        return this.wareHouseRepository.save(wareHouse);
    }

    public WareHouse wareHouseRegistration(UUID userId,
                                      WareHouseRegistrationDTO wareHouseRegistrationDTO){

        //1.validate the id belongs to appAdmin or not.
        //so,what should we.

        AppUser appUser=appUserService.isAppAdmin(userId);

        if(appUser ==null){
            throw new InvalidOperationException(String.format("user with id %s not allowed to register wareHouse",userId.toString()));
        }

        //2.Map details of wareHouseRegistrationDTO and wareHouse model
       // wareHouseRepository.save(userId,wareHouseRegistrationDTO);


        Location location=locationService.createLocation(wareHouseRegistrationDTO.getLocation());

        WareHouse wareHouse=mappingUtility.wareHouseToDTOModel(wareHouseRegistrationDTO,location);
        //3.save this wareHouse object in wareHouse Repository
        wareHouse=this.saveWareHouse(wareHouse);
        //Notify application admin that new warehouse got in your application
        //we need mail service->

        mailService.sendCreateWareHouseMail(wareHouse,appUser);
        return wareHouse;
        
    }
}
