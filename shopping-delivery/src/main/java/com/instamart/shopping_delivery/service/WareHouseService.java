package com.instamart.shopping_delivery.service;

import com.instamart.shopping_delivery.dto.WareHouseItemDTO;
import com.instamart.shopping_delivery.dto.WareHouseRegistrationDTO;
import com.instamart.shopping_delivery.enums.UserType;
import com.instamart.shopping_delivery.exception.InvalidOperationException;
import com.instamart.shopping_delivery.models.*;
import com.instamart.shopping_delivery.repositories.WareHouseItemRepository;
import com.instamart.shopping_delivery.repositories.WareHouseRepository;
import com.instamart.shopping_delivery.utility.MappingUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

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

    public WareHouse findWareHouseAtPinCode(int pinCode){
        //We should chk warehouse table and check is there any wareHouse at this pinCode
        UUID wareHouseId= wareHouseRepository.getWareHouseByLocation(pinCode);
        if(wareHouseId==null){
            return null;
        }
        return this.isValid(wareHouseId);
    }

    public WareHouse getWareHouseByCustomerId(UUID customerId){
        AppUser customer=appUserService.isUser(customerId);
        if(!customer.getUserType().equals(UserType.CUSTOMER.toString())){
            throw new InvalidOperationException(String.format("Customer with id %s is not allowed to see all product",customerId.toString()));
        }

        Location location=locationService.getPrimaryLocation(customer);
        int pinCode=location.getPinCode();
        WareHouse wareHouse=this.findWareHouseAtPinCode(pinCode);
        return wareHouse;
    }


    public List<Product> getAllProductByPinCode(UUID customerId){
        //chk customerID
       WareHouse wareHouse=this.getWareHouseByCustomerId(customerId);
        List<WareHouseItem> wareHouseItems=wareHouse.getWareHouseItems();

        List<Product> products=new ArrayList<>();
        for(WareHouseItem wareHouseItem:wareHouseItems){
            UUID pid=wareHouseItem.getPid();
            //call productService
            Product product=productService.getProductById(pid);
            products.add(product);

        }
        return products;
    }

    public List<WareHouseItemDTO> getProductsAtByName(String name, UUID customerId){
        //product service
        List<Product> products=productService.getProductByName(name);
        //Check the Products that Product present in particular wareHouse YES/NO;

        WareHouse wareHouse=getWareHouseByCustomerId(customerId);
        List<WareHouseItem> wareHouseItem=wareHouse.getWareHouseItems();
        List<WareHouseItemDTO> wareHouseItemDTOS=new ArrayList<>();
        for(int i=0;i<products.size();i++){
            UUID productId=products.get(i).getId();
            String productName=products.get(i).getProductName();
            WareHouseItemDTO wareHouseItemDTO=new WareHouseItemDTO();
            wareHouseItemDTO.setProductId(productId);
            wareHouseItemDTO.setWid(wareHouse.getId());
            wareHouseItemDTO.setProductName(productName);
            wareHouseItemDTO.setPrice(products.get(i).getUnitPrice());
            wareHouseItemDTO.setDiscount(0.0);
            wareHouseItemDTO.setAvailable(false);
            wareHouseItemDTO.setQuantity(products.get(i).getTotalQuantity());

            for(int j=0;j<wareHouseItem.size();j++){
                UUID wareHouseItemId=wareHouseItem.get(j).getPid();

                if(productId.toString().equals(wareHouseItemId.toString())){
                    wareHouseItemDTO.setAvailable(true);
                    wareHouseItemDTO.setDiscount(wareHouseItem.get(j).getDiscount());
                    wareHouseItemDTO.setQuantity(wareHouseItem.get(j).getQuantity());

                }
            }
            wareHouseItemDTOS.add(wareHouseItemDTO);
        }

        AppUser customer =appUserService.isUser(customerId);
        if(!customer.getUserType().equals(UserType.CUSTOMER.toString())){
            throw new InvalidOperationException(String.format("Customer with id %s is not allowed to see all product",customerId.toString()));
        }
        mailService.sendToMailCustomer(customer,wareHouseItem,products);
        return wareHouseItemDTOS;

    }
}
