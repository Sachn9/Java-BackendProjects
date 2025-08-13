package com.instamart.shopping_delivery.service;

import com.instamart.shopping_delivery.dto.OrderProductDTO;
import com.instamart.shopping_delivery.enums.UserType;
import com.instamart.shopping_delivery.exception.InvalidOperationException;
import com.instamart.shopping_delivery.models.*;
import com.instamart.shopping_delivery.repositories.AppOrderRepository;
import com.instamart.shopping_delivery.utility.MappingUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductOrderService {

    MappingUtility mappingUtility;
    AppUserService appUserService;
    WareHouseService wareHouseService;
    DeliveryPartnerService deliveryPartnerService;
    AppOrderRepository appOrderRepository;

    @Autowired
    public ProductOrderService(MappingUtility mappingUtility,
                               AppUserService appUserService,
                               WareHouseService wareHouseService,
                               DeliveryPartnerService deliveryPartnerService,
                               AppOrderRepository appOrderRepository){
        this.mappingUtility=mappingUtility;
        this.appUserService=appUserService;
        this.wareHouseService=wareHouseService;
        this.deliveryPartnerService=deliveryPartnerService;
        this.appOrderRepository=appOrderRepository;
    }

    public AppOrder createOrder(OrderProductDTO orderProductDTO, UUID shopperId) {
        AppOrder appOrder = mappingUtility.mapOrderProductDtoToAppOrderModel(orderProductDTO);


        AppUser shopper = appUserService.isUser(shopperId);
        if (!shopper.getUserType().equals(UserType.CUSTOMER.toString())) {
            throw new InvalidOperationException(String.format("Customer with id %s is not allowed to see all product", shopperId.toString()));
        }

        //Get the customer pinCode
        int pinCode = shopper.getLocation().get(0).getPinCode();
        //Help of customerPinCode get the wareHouse

        WareHouse wareHouse = wareHouseService.findWareHouseAtPinCode(pinCode);

        List<WareHouseItem> wareHouseItems = wareHouse.getWareHouseItems();
        //fond the productId in wareHouseItems Table
        WareHouseItem matchItem = null;

        for (WareHouseItem item : wareHouseItems) {
            if (item.getId().equals(orderProductDTO.getProductId())) {
                matchItem = item;
                break;
            }
        }

        if (matchItem == null) {
            throw new InvalidOperationException("Product not available in warehouse for your location");
        }

        //  Check stock
        if (matchItem.getQuantity() < orderProductDTO.getQuantity()) {
            throw new InvalidOperationException("Not enough stock available");
        }

        //reduce stock
        matchItem.setQuantity(matchItem.getQuantity()-orderProductDTO.getQuantity());
        //wareHouseItems.add(matchItem);

        //Add product to order
        appOrder.setProducts(matchItem.getProductList());
        //Allocate delivery partner
        List<AppUser> deliveryPartner=wareHouse.getDeliveryPartner();

        AppUser dPartner=null;
        for(AppUser dp:deliveryPartner){
            if(dp.getStatus()=="ACTIVE"){
                dPartner=dp;
                break;
            }
        }
        if(dPartner==null){
            throw new InvalidOperationException("Delivery partner is not Active");
        }

        appOrder.setDeliveryPartner(dPartner);
        appOrder.setTotalItems(appOrder.getTotalItems());
        appOrderRepository.save(appOrder);
        return appOrder;



    }


}
