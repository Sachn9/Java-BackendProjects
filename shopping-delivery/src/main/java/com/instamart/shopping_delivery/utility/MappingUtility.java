package com.instamart.shopping_delivery.utility;

import com.instamart.shopping_delivery.dto.OrderProductDTO;
import com.instamart.shopping_delivery.dto.ProductDTO;
import com.instamart.shopping_delivery.dto.WareHouseRegistrationDTO;
import com.instamart.shopping_delivery.models.*;
import com.instamart.shopping_delivery.service.WareHouseService;
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

    public Product mapProductDTOToProductModel(ProductDTO productDTO,
                                               AppUser user){
        Product product=new Product();
        product.setProductName(productDTO.getProductName());
        product.setDescription(productDTO.getDescription());
        product.setOwnerCompany(productDTO.getOwnerCompany());
        product.setTotalQuantity(productDTO.getTotalQuantity());
        product.setUnitPrice(productDTO.getUnitPrice());
        product.setCreateBy(user);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return product;
    }

    public AppOrder mapOrderProductDtoToAppOrderModel(OrderProductDTO orderProductDTO){
        AppOrder appOrder=new AppOrder();
        appOrder.setStatus(orderProductDTO.getStatus());
        appOrder.setPaymentType(orderProductDTO.getPaymentType());
        appOrder.setCreatedAt(LocalDateTime.now());
        appOrder.setUpdatedAt(LocalDateTime.now());
        return appOrder;
    }

}
