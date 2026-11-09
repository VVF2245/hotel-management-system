package com.hotel.hotel_management.user;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Reception staff: operates guests, bookings, check-in and check-out, and requests
 * in-stay cleaning.
 */
@Entity
@DiscriminatorValue("RECEPTIONIST")
public class Receptionist extends User {
}
