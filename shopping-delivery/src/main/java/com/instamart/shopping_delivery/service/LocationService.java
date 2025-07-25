package com.instamart.shopping_delivery.service;

import com.instamart.shopping_delivery.dto.WareHouseRegistrationDTO;
import com.instamart.shopping_delivery.models.Location;
import com.instamart.shopping_delivery.repositories.LocationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


/*
    this class contains all logics related to Location
 */
@Service
@Slf4j
public class LocationService {

    LocationRepository locationRepository;
    @Autowired
    public LocationService(LocationRepository locationRepository){
        this.locationRepository=locationRepository;
    }
    /*
            Work this function to save the location object in location table
     */
    public Location createLocation(Location location){

        //we want to the save location object in location table
        //That means we require the locationRepository which save the location in location table
        location.setCreatedAt(LocalDateTime.now());
        location.setUpdatedAt(LocalDateTime.now());
        return locationRepository.save(location);

    }


}
