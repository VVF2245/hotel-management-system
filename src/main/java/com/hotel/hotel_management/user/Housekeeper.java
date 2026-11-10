package com.hotel.hotel_management.user;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Housekeeping staff: views, starts and completes the cleaning tasks assigned to them.
 */
@Entity
@DiscriminatorValue("HOUSEKEEPER")
public class Housekeeper extends User {
}
