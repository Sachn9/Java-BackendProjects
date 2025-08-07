package com.instamart.shopping_delivery.controller;

import com.instamart.shopping_delivery.dto.ProductDTO;
import org.hibernate.query.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/product")
public class OrderController {

    public ResponseEntity<> createOrder(@RequestBody ProductDTO productDTO){
        createOrder(productDTO);
    }
}
