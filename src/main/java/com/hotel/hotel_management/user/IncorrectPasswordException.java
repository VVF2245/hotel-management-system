package com.hotel.hotel_management.user;

/**
 * Thrown when the current password supplied by a user does not match the stored hash.
 * The message must never include the password itself.
 */
public class IncorrectPasswordException extends RuntimeException {

    public IncorrectPasswordException(String message) {
        super(message);
    }

}
