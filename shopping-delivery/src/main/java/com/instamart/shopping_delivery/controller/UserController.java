package com.instamart.shopping_delivery.controller;

import com.instamart.shopping_delivery.exception.InvalidOperationException;
import com.instamart.shopping_delivery.exception.UserNotExitException;
import com.instamart.shopping_delivery.exception.WareHouseDoesNotExist;
import com.instamart.shopping_delivery.models.AppUser;
import com.instamart.shopping_delivery.service.AppUserService;
import com.instamart.shopping_delivery.service.DeliveryPartnerService;
import com.instamart.shopping_delivery.service.MailService;
import jakarta.persistence.Entity;
import org.apache.tomcat.util.net.jsse.JSSEUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ResourceBundle;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    AppUserService appUserService;
    DeliveryPartnerService deliveryPartnerService;

    @Autowired
    public UserController(AppUserService appUser,
                          DeliveryPartnerService deliveryPartnerService){

        this.appUserService=appUser;
        this.deliveryPartnerService=deliveryPartnerService;
    }
    @PostMapping("/customer/registration")
    public AppUser customerRegistration(@RequestBody AppUser customer){
        //call customerService/AppUserService
        System.out.println(customer);
        //customerService
        AppUser user= appUserService.userRegistration(customer);
        return user;
    }

    @PostMapping("/wareHouse/admin/invite")
    public ResponseEntity wareHouseAdminInvite(@RequestParam UUID userId,
                                               @RequestBody  AppUser wareHouseAdmin){

        //AppUserService -> wareHouse admin invite
        try {
            appUserService.wareHouseAdminInvite(userId,wareHouseAdmin);
            return new ResponseEntity<>("Inactive recode inside the user table", HttpStatus.CREATED);
        }catch (InvalidOperationException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.UNAUTHORIZED);
        }catch (UserNotExitException e){
           return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
       }

    }

    @GetMapping("/wareHouse/admin/accept/invite/{wareHouseAdminId}")
    public void acceptWareHouseAdminInvite(@PathVariable UUID wareHouseAdminId){
        appUserService.acceptWareHouseAdminInvite(wareHouseAdminId);
    }

    /**
     * This Particular function will recieve request from the client to register delivery Partner.
     * @param deliveryPartner
     */
    @PostMapping("/deliveryPartner/registration")
    public ResponseEntity registrationDeliveryPartner(@RequestBody AppUser deliveryPartner){
        //DeliveryPartner Service
        try{
            deliveryPartnerService.registerDeliveryPartner(deliveryPartner);
            return new ResponseEntity<>(deliveryPartner,HttpStatus.CREATED);
        }catch (WareHouseDoesNotExist wareHouseDoesNotExist){
            return new ResponseEntity<>(wareHouseDoesNotExist.getMessage(),HttpStatus.NOT_FOUND);

        }



    }
}
