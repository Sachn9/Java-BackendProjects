package com.instamart.shopping_delivery.exception;

public class WareHouseDoesNotExist extends RuntimeException {
    public WareHouseDoesNotExist(String message) {
      super(message);
    }
}
