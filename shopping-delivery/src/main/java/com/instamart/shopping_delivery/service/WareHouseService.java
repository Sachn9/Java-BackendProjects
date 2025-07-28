package com.instamart.shopping_delivery.service;

import com.instamart.shopping_delivery.dto.WareHouseRegistrationDTO;
import com.instamart.shopping_delivery.exception.InvalidOperationException;
import com.instamart.shopping_delivery.models.*;
import com.instamart.shopping_delivery.repositories.WareHouseItemRepository;
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
    ProductService productService;
    WareHouseItemRepository wareHouseItemRepository;
    @Autowired
    public WareHouseService(AppUserService appUserService,
                            WareHouseRepository wareHouseRepository,
                            LocationService locationService,
                            MappingUtility mappingUtility,
                            MailService mailService,
                            ProductService productService,
                            WareHouseItemRepository wareHouseItemRepository){
        this.appUserService=appUserService;
        this.wareHouseRepository=wareHouseRepository;
        this.locationService=locationService;
        this.mappingUtility=mappingUtility;
        this.mailService=mailService;
        this.productService=productService;
        this.wareHouseItemRepository=wareHouseItemRepository;

    }


    public WareHouse saveWareHouse(WareHouse wareHouse){
        return this.wareHouseRepository.save(wareHouse);
    }

    public WareHouseRegistrationDTO wareHouseRegistration(UUID userId,
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
         return wareHouseRegistrationDTO;
        
    }

    public WareHouse isValid(UUID wid){
        WareHouse wareHouse=wareHouseRepository.findById(wid).orElse(null);
        return wareHouse;

    }

    public WareHouseItem assignProductToWareHouse(WareHouseItem wareHouseItem,
                                                  UUID userId){

        AppUser user=appUserService.isAppAdmin(userId);
        if(user==null){
            throw new InvalidOperationException(String.format("User id with %s does not exist",userId.toString()));
        }

        UUID wid=wareHouseItem.getWid();
        UUID pid=wareHouseItem.getPid();
        //Validate both the ids correct or not?
        WareHouse wareHouse=isValid(wid);
        if(wareHouse==null){
            throw new InvalidOperationException(String.format("wareHouse id with %s does not have exist",wid.toString()));
        }

        Product product=productService.isValid(pid);
        if(product==null){
            throw new InvalidOperationException(String.format("Product id with %s does not have exit",pid.toString()));
        }
        int totalQuantity=product.getTotalQuantity();
        if(totalQuantity < wareHouseItem.getQuantity()){
            throw new InvalidOperationException("It quantity can't assign Because TotalQuantity is less then require quantity");
        }
        product.setTotalQuantity(totalQuantity - wareHouseItem.getQuantity());
        //The changes we made need to be updated in the productRepository.
        productService.updateProduct(product);
        wareHouseItem.setCreatedAt(LocalDateTime.now());
        wareHouseItem.setUpdatedAt(LocalDateTime.now());
        //Save the WareHouseItem in WareHouseItemRepository
        wareHouseItem=wareHouseItemRepository.save(wareHouseItem);
        //This wareHouse Item allocate the one particular wareHouse

        wareHouse.getWareHouseItems().add(wareHouseItem);

        //And this wareHouse changes to update wareHouseRepository
        saveWareHouse(wareHouse);
        mailService.sendAssignWareHouseItemToAppAdmin(wareHouseItem,user);
//        mailService.sendMailAssignWareHouseItemToWareHouseAdmin(wareHouseItem,);
        return wareHouseItem;
    }


    public WareHouseRegistrationDTO assignManagerToWarehouse(UUID appAdmin,
                                         UUID wareHouseId,
                                         UUID wareHouseAdminId){
        //verify the all ids

        AppUser appUser=appUserService.isAppAdmin(appAdmin);
        if(appUser == null){
            throw new InvalidOperationException(String.format("User id %s does Not Exist",appAdmin.toString()));
        }
        WareHouse wareHouse=isValid(wareHouseId);
        if(wareHouse==null){
            throw new InvalidOperationException(String.format("wareHouse id with %s does not have exist",wareHouseId.toString()));
        }

        AppUser wareHouseAdmin=appUserService.isWareHouseAdminId(wareHouseAdminId);
        if(wareHouseAdmin==null){
            throw new InvalidOperationException(String.format("WareHouse Admin ID %s does not have exist",wareHouseAdminId.toString()));
        }

        wareHouse.setManager(wareHouseAdmin);
        wareHouseRepository.save(wareHouse);
        //send the email wareHouseAdmin that you assign the wareHouseManager;
        mailService.sendEmailToWareHouseAdminAssignManager(wareHouse,wareHouseAdmin);

        WareHouseRegistrationDTO wareHouseRegistrationDTO=new WareHouseRegistrationDTO();
        wareHouseRegistrationDTO.setWareHouseName(wareHouse.getName());
        wareHouseRegistrationDTO.setLocation(wareHouse.getLocation());
        return wareHouseRegistrationDTO;

    }
}
