package com.hotel.hotel_management.guest;

import com.hotel.hotel_management.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Person associated with bookings. The identity document is optional, but when it is
 * recorded both its type and its number are required.
 */
@Getter
@Setter
@Entity
public class Guest extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String firstName;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private DocumentType documentType;

    @Size(min = 1, max = 20)
    @Column(length = 20)
    private String documentNumber;

    @Size(min = 1, max = 20)
    @Column(length = 20)
    private String phone;

    @Email
    @Size(max = 254)
    @Column(length = 254)
    private String email;

}
