package com.instamart.shopping_delivery.service;

import com.instamart.shopping_delivery.exception.InvalidOperationException;
import com.instamart.shopping_delivery.exception.UserNotExitException;
import com.instamart.shopping_delivery.models.AppUser;
import com.instamart.shopping_delivery.models.Location;
import com.instamart.shopping_delivery.repositories.AppUserRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.Properties;
import java.util.UUID;

@Service
public class AppUserService {

    AppUserRepository appUserRepository;
    MailService mailService;
    LocationService locationService;
    @Autowired
    public AppUserService(AppUserRepository appUserRepository,
                          MailService mailService,
                          LocationService locationService){

        this.appUserRepository=appUserRepository;
        this.mailService=mailService;
        this.locationService=locationService;
    }

    public AppUser userRegistration(AppUser user){
        Location location=user.getLocation().get(0);
        locationService.createLocation(location);
         return appUserRepository.save(user);

    }

    public AppUser wareHouseAdminInvite(UUID userId,AppUser wareHouseAdmin){
        //1.chk the userId that he is appAdmin yes/no
        AppUser admin=appUserRepository.findById(userId).orElse(null);
        if(admin == null) {
            //Throw exception that user does not exit
            throw new UserNotExitException(String.format("user with id %s does not exit",userId.toString()));

        }

        if(!admin.getUserType().equals("APP_ADMIN")){
            throw new InvalidOperationException("user not allowed to invite wareHouse admin");
        }

        wareHouseAdmin.setStatus("INACTIVE");
        wareHouseAdmin=appUserRepository.save((wareHouseAdmin));
        mailService.sendWareHouseInvitationMail(wareHouseAdmin);

        return wareHouseAdmin;
    }

    public void acceptWareHouseAdminInvite(UUID wareHouseAdminId){
        AppUser user=appUserRepository.findById(wareHouseAdminId).orElse(null);
        user.setStatus("ACTIVE");
        appUserRepository.save(user);

    }

    public AppUser isAppAdmin(UUID userId){
        AppUser user=appUserRepository.findById(userId).orElse(null);
        if(user.getUserType().equals("APP_ADMIN")){
            return user;
        }
        return null;

    }

    public AppUser isWareHouseAdminId(UUID wareHouseAdminId){

        AppUser wareHouseAdmin=appUserRepository.findById(wareHouseAdminId).orElse(null);
        if(wareHouseAdmin.getUserType().equals("WAREHOUSE_ADMIN")){
            return wareHouseAdmin;
        }
        return null;
    }

    public AppUser isUser(UUID customerId){
        AppUser user=appUserRepository.findById(customerId).orElse(null);
        return user;

    }

}
