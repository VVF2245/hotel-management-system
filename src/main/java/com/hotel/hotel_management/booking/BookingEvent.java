package com.hotel.hotel_management.booking;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.hotel.hotel_management.common.BaseEntity;
import com.hotel.hotel_management.user.Receptionist;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Immutable audit record of a booking lifecycle action (US-11).
 */
@Getter
@Setter
@Entity
public class BookingEvent extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false, length = 20)
    private BookingEventType eventType;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime occurredAt;

    /** Description of the changed fields, used for MODIFIED events. */
    @Column(updatable = false, length = 65535)
    private String changedData;

    /** Optional reason, for example a cancellation reason. */
    @Size(min = 1, max = 500)
    @Column(updatable = false, length = 500)
    private String reason;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false, updatable = false)
    private Booking booking;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "performed_by_id", nullable = false, updatable = false)
    private Receptionist performedBy;

}
