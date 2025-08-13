package com.instamart.shopping_delivery.service;

import com.instamart.shopping_delivery.dto.OrderProductDTO;
import com.instamart.shopping_delivery.enums.UserType;
import com.instamart.shopping_delivery.exception.InvalidOperationException;
import com.instamart.shopping_delivery.models.*;
import com.instamart.shopping_delivery.repositories.AppOrderRepository;
import com.instamart.shopping_delivery.repositories.WareHouseItemRepository;
import com.instamart.shopping_delivery.utility.MappingUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ProductOrderService {

    MappingUtility mappingUtility;
    AppUserService appUserService;
    WareHouseService wareHouseService;
    DeliveryPartnerService deliveryPartnerService;
    AppOrderRepository appOrderRepository;
    WareHouseItemRepository wareHouseItemRepository;

    @Autowired
    public ProductOrderService(MappingUtility mappingUtility,
                               AppUserService appUserService,
                               WareHouseService wareHouseService,
                               DeliveryPartnerService deliveryPartnerService,
                               AppOrderRepository appOrderRepository,
                               WareHouseItemRepository wareHouseItemRepository){
        this.mappingUtility=mappingUtility;
        this.appUserService=appUserService;
        this.wareHouseService=wareHouseService;
        this.deliveryPartnerService=deliveryPartnerService;
        this.appOrderRepository=appOrderRepository;
        this.wareHouseItemRepository=wareHouseItemRepository;
    }

    public AppOrder createOrder(OrderProductDTO orderProductDTO, UUID shopperId) {
        AppOrder appOrder = mappingUtility.mapOrderProductDtoToAppOrderModel(orderProductDTO);


        AppUser shopper = appUserService.isUser(shopperId);
        if (!shopper.getUserType().equals(UserType.CUSTOMER.toString())) {
            throw new InvalidOperationException(String.format("Customer with id %s is not allowed to see all product", shopperId.toString()));
        }

        // set shopper on order
        appOrder.setShopper(shopper);

        // validate DTO
        if (orderProductDTO == null) {
            log.warn("OrderProductDTO is null for shopper {}", shopperId);
            throw new InvalidOperationException("Invalid order payload");
        }
        if (orderProductDTO.getProductId() == null) {
            throw new InvalidOperationException("Product ID is required");
        }
        if (orderProductDTO.getQuantity() <= 0) {
            throw new InvalidOperationException("Quantity must be greater than zero");
        }

        //Get the customer pinCode
        List<Location> locations = shopper.getLocation();
        if (locations == null || locations.isEmpty()) {
            log.warn("No locations found for shopper {}", shopperId);
            throw new InvalidOperationException("Customer location not set");
        }
        int pinCode = locations.get(0).getPinCode();
        //Help of customerPinCode get the wareHouse

        WareHouse wareHouse = wareHouseService.findWareHouseAtPinCode(pinCode);
        if (wareHouse == null) {
            log.warn("No warehouse found at pincode {}", pinCode);
            throw new InvalidOperationException("Service not available at your location");
        }

        List<WareHouseItem> wareHouseItems = wareHouse.getWareHouseItems();
        if (wareHouseItems == null || wareHouseItems.isEmpty()) {
            throw new InvalidOperationException("No inventory found at warehouse for your location");
        }
        //fond the productId in wareHouseItems Table
        WareHouseItem matchItem = null;

        for (WareHouseItem item : wareHouseItems) {
            if (item.getPid().equals(orderProductDTO.getProductId())) {
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
        // persist stock change
        wareHouseItemRepository.save(matchItem);

        // Build order item (using first product in productList for now)
        List<Product> products = matchItem.getProductList();
        if (products == null || products.isEmpty()) {
            throw new InvalidOperationException("Warehouse item has no associated product");
        }
        Product product = products.get(0);

        OderItems orderItem = new OderItems();
        orderItem.setOrder(appOrder);
        orderItem.setProduct(product);
        orderItem.setQuantity(orderProductDTO.getQuantity());
        BigDecimal unitPrice = BigDecimal.valueOf(product.getUnitPrice());
        orderItem.setUnitPrice(unitPrice);
        orderItem.setLineTotal(unitPrice.multiply(BigDecimal.valueOf(orderProductDTO.getQuantity())));
        orderItem.setCreatedAt(LocalDateTime.now());
        orderItem.setUpdatedAt(LocalDateTime.now());

        if (appOrder.getItems() == null) {
            appOrder.setItems(new ArrayList<>());
        }
        appOrder.getItems().add(orderItem);

        // set totals
        appOrder.setTotalItems(orderProductDTO.getQuantity());
        appOrder.setTotalPrice(orderItem.getLineTotal());

        //Allocate delivery partner
        List<AppUser> deliveryPartner=wareHouse.getDeliveryPartner();
        if (deliveryPartner == null || deliveryPartner.isEmpty()) {
            throw new InvalidOperationException("No delivery partners available at the moment");
        }

        AppUser dPartner=null;
        for(AppUser dp:deliveryPartner){
            if("ACTIVE".equals(dp.getStatus())){
                dPartner=dp;
                break;
            }
        }
        if(dPartner==null){
            throw new InvalidOperationException("Delivery partner is not Active");
        }

        appOrder.setDeliveryPartner(dPartner);
        appOrderRepository.save(appOrder);
        return appOrder;



    }


}
