package com.instamart.shopping_delivery.service;

import com.instamart.shopping_delivery.enums.UserStatusEnum;
import com.instamart.shopping_delivery.enums.UserType;
import com.instamart.shopping_delivery.exception.InvalidOperationException;
import com.instamart.shopping_delivery.exception.WareHouseDoesNotExist;
import com.instamart.shopping_delivery.models.AppUser;
import com.instamart.shopping_delivery.models.Location;
import com.instamart.shopping_delivery.models.WareHouse;
import com.instamart.shopping_delivery.repositories.AppUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class DeliveryPartnerService {

    @Autowired
    WareHouseService wareHouseService;
    LocationService locationService;
    AppUserService appUserService;
    MailService mailService;
    AppUserRepository appUserRepository;

    public DeliveryPartnerService(WareHouseService wareHouseService,
                                  LocationService locationService,
                                  AppUserService appUserService,
                                  MailService mailService,
                                  AppUserRepository appUserRepository){
        this.wareHouseService=wareHouseService;
        this.locationService=locationService;
        this.appUserService=appUserService;
        this.mailService=mailService;
        this.appUserRepository=appUserRepository;
    }
    /**
     * This function contain logic to saveDelivery partner object inside the database.
     * And we will have status of delivery partner as inactive.
     * And we will be mailing to the wareHouseAdmin regarding delivery partner registration.
     */

    public AppUser registerDeliveryPartner(AppUser deliveryPartner){
        // Customer can have multiple location but delivery partner can have one location
        int pinCode=deliveryPartner.getLocation().get(0).getPinCode();
        //Find wareHouse at pinCode
        WareHouse wareHouse=wareHouseService.findWareHouseAtPinCode(pinCode);

        if(wareHouse==null){
            throw new WareHouseDoesNotExist(String.format("WareHouse at pinCode %d does not exist",pinCode));
        }

        deliveryPartner.setStatus(UserStatusEnum.INACTIVE.toString());

        Location location=deliveryPartner.getLocation().get(0);
        //Location save to dataBase
        location=locationService.createLocation(location);
        deliveryPartner.getLocation().set(0,location);
        //deliveryPartner ko save karna h user table ke ander
        appUserService.userRegistration(deliveryPartner);

        //We need to send the email wareHose Admin regarding deliveryPartner Registration
        mailService.sendMailToWareHouseAdminRegardingTODeliveryPartnerRegistration(deliveryPartner,wareHouse.getManager().getEmail());
        return deliveryPartner;


    }

    public void acceptDeliveryPartner(UUID deliveryPartnerId){
        AppUser deliveryPartner=appUserService.isUser(deliveryPartnerId);
        if( !deliveryPartner.getUserType().equals(UserType.DELIVERY_PARTNER.toString())){
            throw new InvalidOperationException(String.format(" This ID %s is not deliveryPartner",deliveryPartnerId.toString()));
        }
        deliveryPartner.setStatus("Active");
        appUserRepository.save(deliveryPartner);

        int pinCode=deliveryPartner.getLocation().get(0).getPinCode();
        WareHouse wareHouse=wareHouseService.findWareHouseAtPinCode(pinCode);
        if(wareHouse==null){
            throw new InvalidOperationException(String.format("this pinCode %d doesn't exit wareHouse",pinCode));
        }

        wareHouse.getDeliveryPartner().add(deliveryPartner);
        wareHouseService.saveWareHouse(wareHouse);



    }

}
