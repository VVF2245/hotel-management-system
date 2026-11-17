package com.hotel.hotel_management.user;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("User " + id + " not found");
    }
    
}
