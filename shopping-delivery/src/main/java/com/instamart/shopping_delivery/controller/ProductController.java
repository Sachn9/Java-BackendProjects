package com.instamart.shopping_delivery.controller;

import com.instamart.shopping_delivery.dto.ProductDTO;
import com.instamart.shopping_delivery.dto.WareHouseItemDTO;
import com.instamart.shopping_delivery.exception.InvalidOperationException;
import com.instamart.shopping_delivery.models.Location;
import com.instamart.shopping_delivery.models.Product;
import com.instamart.shopping_delivery.models.WareHouseItem;
import com.instamart.shopping_delivery.service.ProductService;
import com.instamart.shopping_delivery.service.WareHouseService;
import jakarta.persistence.GeneratedValue;
import jdk.dynalink.linker.LinkerServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@Slf4j
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

    @GetMapping("/all")
    public ResponseEntity getAllProductByPinCode(@RequestParam UUID customerId){
        //call wareHouseService
        try{
            List<Product> products=wareHouseService.getAllProductByPinCode(customerId);
            return new ResponseEntity<>(products,HttpStatus.OK);
        }catch (InvalidOperationException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.UNAUTHORIZED);
        }catch(Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/search")
    public ResponseEntity getAllProductByName(@RequestParam String name,
                                             @RequestParam UUID customerId){
        //wareHouse Service
        try {
            List<WareHouseItemDTO> wareHouseItemDTO = wareHouseService.getProductsAtByName(name, customerId);
            return new ResponseEntity<>(wareHouseItemDTO, HttpStatus.OK);
        }catch (InvalidOperationException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.UNAUTHORIZED);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }




}
