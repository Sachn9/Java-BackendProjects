package com.instamart.shopping_delivery.repositories;

import com.instamart.shopping_delivery.dto.WareHouseRegistrationDTO;
import com.instamart.shopping_delivery.models.WareHouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WareHouseRepository extends JpaRepository<WareHouse, UUID> {

    @Query(value = "SELECT warehouses.id\n" +
            "FROM warehouses\n" +
            "INNER JOIN locations ON warehouses.location_id = locations.id\n" +
            "WHERE locations.pin_code = :pinCode;",nativeQuery = true)
    public UUID getWareHouseByLocation(int pinCode);


}
