package com.hotel.hotel_management.booking;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.hotel.hotel_management.common.BaseEntity;
import com.hotel.hotel_management.guest.Guest;
import com.hotel.hotel_management.room.Room;
import com.hotel.hotel_management.user.Receptionist;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * A guest's stay in a room. The check-in date is inclusive and the check-out date is
 * exclusive (BR-01). The cancellation reason is stored in the CANCELLED {@link BookingEvent}.
 */
@Getter
@Setter
@Entity
public class Booking extends BaseEntity {

    @NotNull
    @Column(nullable = false)
    private LocalDate checkInDate;

    @NotNull
    @Column(nullable = false)
    private LocalDate checkOutDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus bookingStatus;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_id", nullable = false, updatable = false)
    private Receptionist createdBy;

}
