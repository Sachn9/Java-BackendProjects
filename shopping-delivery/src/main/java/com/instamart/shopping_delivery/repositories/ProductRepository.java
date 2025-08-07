package com.instamart.shopping_delivery.repositories;

import com.instamart.shopping_delivery.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query(value = "select * from products where product_name like :namePattern",nativeQuery = true)
    public List<Product> getProductByName(String namePattern);
}
