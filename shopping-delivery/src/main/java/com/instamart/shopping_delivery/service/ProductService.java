package com.instamart.shopping_delivery.service;

import com.instamart.shopping_delivery.dto.ProductDTO;
import com.instamart.shopping_delivery.enums.UserType;
import com.instamart.shopping_delivery.exception.InvalidOperationException;
import com.instamart.shopping_delivery.models.AppUser;
import com.instamart.shopping_delivery.models.Location;
import com.instamart.shopping_delivery.models.Product;
import com.instamart.shopping_delivery.models.WareHouse;
import com.instamart.shopping_delivery.repositories.ProductRepository;
import com.instamart.shopping_delivery.utility.MappingUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.UUID;

@Service
public class ProductService {


    AppUserService appUserService;
    ProductRepository productRepository;
    MappingUtility mappingUtility;
    MailService mailService;
    LocationService locationService;

    @Autowired
    public ProductService(AppUserService appUserService,
                          ProductRepository productRepository,
                          MappingUtility mappingUtility,
                          MailService mailService,
                          LocationService locationService){
        this.appUserService=appUserService;
        this.productRepository=productRepository;
        this.mappingUtility=mappingUtility;
        this.mailService=mailService;
        this.locationService=locationService;
    }

    public Product save(Product product){
        return this.productRepository.save(product);
    }

    public ProductDTO addProduct(ProductDTO productDTO,
                           UUID userId){
        AppUser user=appUserService.isAppAdmin(userId);
        if(user==null){
            throw new InvalidOperationException(String.format("User with id %s does not have access to create product",userId));
        }

        /*
        1.Now we will save product in the product table
        2.So, To Save the product in product table require the product repository
        3.As we can save product table object in product table
        4.But wc can't save direct ProductDto in projectRepository
        5.First we will map ProductDTO and product model
         */

        Product product=this.mappingUtility.mapProductDTOToProductModel(productDTO,user);
        product= this.save(product);

        mailService.sendADDProductionMail(product,user);
        productDTO.setId(product.getId());
        return productDTO;

    }

    public Product isValid(UUID pid){
        Product product=productRepository.findById(pid).orElse(null);
        return product;
    }

    public void updateProduct(Product product){
        productRepository.save(product);
    }


    public  Product getProductById(UUID pid){
        return productRepository.findById(pid).orElse(null);

    }

    public List<Product> getProductByName(String name){
        //Product repository
        String namePattern="%"+name+"%";
        List<Product> products=productRepository.getProductByName(namePattern);
        return products;
    }
}
