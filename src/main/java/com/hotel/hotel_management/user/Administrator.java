package com.hotel.hotel_management.user;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * System and supervision role: manages users and rooms and reviews the history and the
 * dashboard. It does not operate bookings, guests or cleaning tasks, so no operational
 * entity references it.
 */
@Entity
@DiscriminatorValue("ADMINISTRATOR")
public class Administrator extends User {
}
