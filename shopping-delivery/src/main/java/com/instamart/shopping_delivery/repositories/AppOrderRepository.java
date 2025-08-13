package com.instamart.shopping_delivery.repositories;

import com.instamart.shopping_delivery.models.AppOrder;
import com.instamart.shopping_delivery.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AppOrderRepository extends JpaRepository<AppOrder, UUID> {

}
