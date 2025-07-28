package com.instamart.shopping_delivery.controller;

import com.instamart.shopping_delivery.dto.ProductDTO;
import com.instamart.shopping_delivery.models.Product;
import com.instamart.shopping_delivery.models.WareHouseItem;
import com.instamart.shopping_delivery.service.ProductService;
import com.instamart.shopping_delivery.service.WareHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

    ProductService productService;
    WareHouseService wareHouseService;
    @Autowired
    public ProductController(ProductService productService,
                             WareHouseService wareHouseService){
        this.productService=productService;
        this.wareHouseService=wareHouseService;
    }

    /*
       This function will run when api/v1/product/add EndPoint will get triggered;
     */
    @PostMapping("/add")
    public ResponseEntity addProduct(@RequestBody ProductDTO productDTO,
                                     @RequestParam UUID userId){

        //We need to product service add to product
        ProductDTO product=this.productService.addProduct(productDTO,userId);
        return new ResponseEntity(product, HttpStatus.CREATED);

    }

    @PostMapping("/assign")
    public ResponseEntity assignProductToWareHouse(@RequestBody WareHouseItem wareHouseItem,
                                                   @RequestParam UUID userId){
        wareHouseItem=wareHouseService.assignProductToWareHouse(wareHouseItem,userId);
        return new ResponseEntity(wareHouseItem,HttpStatus.CREATED);

    }
}
