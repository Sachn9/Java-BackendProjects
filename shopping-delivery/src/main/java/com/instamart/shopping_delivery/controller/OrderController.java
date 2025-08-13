package com.instamart.shopping_delivery.controller;

import com.instamart.shopping_delivery.dto.OrderProductDTO;
import com.instamart.shopping_delivery.dto.ProductDTO;
import com.instamart.shopping_delivery.exception.InvalidOperationException;
import com.instamart.shopping_delivery.models.AppOrder;
import com.instamart.shopping_delivery.service.ProductOrderService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/product")
public class OrderController {

    ProductOrderService productOrderService;
    @Autowired
    public OrderController(ProductOrderService productOrderService){
        this.productOrderService=productOrderService;
    }


    @PostMapping("/order")
    public ResponseEntity createOrder(@RequestBody OrderProductDTO orderProductDTO,
                                      @RequestParam UUID shopperId){
        try{
            AppOrder appOrder=productOrderService.createOrder(orderProductDTO,shopperId);
            return new ResponseEntity<>(appOrder, HttpStatus.OK);
        }catch(InvalidOperationException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.UNAUTHORIZED);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
