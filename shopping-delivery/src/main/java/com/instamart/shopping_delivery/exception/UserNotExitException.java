package com.instamart.shopping_delivery.exception;

public class UserNotExitException extends RuntimeException{
    public UserNotExitException(String message){
        super(message);
    }
}
