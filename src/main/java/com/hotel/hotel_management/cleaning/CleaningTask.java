package com.hotel.hotel_management.cleaning;

import java.time.LocalDateTime;

import com.hotel.hotel_management.common.BaseEntity;
import com.hotel.hotel_management.room.Room;
import com.hotel.hotel_management.user.Housekeeper;
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
 * Cleaning work for a room. Its lifecycle is derived from the timestamps: no startedAt
 * means pending, startedAt without completedAt means in progress, completedAt means completed.
 */
@Getter
@Setter
@Entity
public class CleaningTask extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false, length = 20)
    private CleaningTaskOrigin origin;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    @Size(min = 1, max = 500)
    @Column(length = 500)
    private String notes;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false, updatable = false)
    private Room room;

    /** Null for tasks created automatically at check-out. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by_id", updatable = false)
    private Receptionist requestedBy;

    /** Null until the task is assigned to a housekeeper. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private Housekeeper assignedTo;

}
