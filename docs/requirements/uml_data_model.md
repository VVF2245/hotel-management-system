# UML Data Model Guide

This document gathers the candidate entities, attributes, and relationships derived from the user stories and the client interview. It is a starting point for a class diagram or entity-relationship model; it does not prescribe database types or implementation details.

## Candidate entities and attributes

### User (supertype)

Represents a staff access account. In the UML diagram, `User` is the supertype and does not have a `role` attribute: the user type is represented by its subtypes.

* `id`
* `firstName`
* `lastName`
* `username` — unique
* `email`
* `passwordHash` — never store the original password
* `accountStatus` — active or inactive
* `createdAt`

### User subtypes

* `ReceptionUser`: a member of the reception staff.
* `HousekeepingUser`: a member of the housekeeping staff.
* `AdministratorUser`: a user with administrative permissions.

All three subtypes inherit the attributes of `User`. In this version, each account belongs to exactly one subtype.

### Room

Represents a room managed by the hotel.

* `id`
* `number` — unique
* `type` — for example, single, double, or suite
* `pricePerNight`
* `notes` — optional
* `active`

The room's current occupancy is derived from its bookings, rather than stored as a room attribute. Cleaning is represented independently by `CleaningTask`, so a room may have an in-progress booking and an active cleaning task at the same time. Availability for a date range is calculated from overlapping bookings and is not a separate room attribute. A room is ready for a new stay when it has no in-progress booking and no pending or in-progress post-check-out cleaning task.

### Guest

Represents a person associated with a booking.

* `id`
* `firstName`
* `lastName`
* `identityDocument` — optional
* `phone` — optional
* `email` — optional

First and last names are considered required as an analysis assumption; the interview only confirms that the guest's name is recorded.

### Booking

Represents a requested or completed stay. Its `status` stores the current lifecycle state; `BookingEvent` records the audit history and is not used to recalculate the current state on every read.

* `id`
* `checkInDate`
* `checkOutDate`
* `status` — confirmed, in progress, completed, or cancelled
* `createdAt`
* `cancellationReason` — optional; this may instead be stored in the booking history event

A booking must reference a guest, a room, and the user who created it. Later events are modelled in `BookingEvent`.

### BookingEvent

Represents an auditable event in a booking's lifecycle.

* `id`
* `type` — creation, modification, cancellation, check-in, or check-out
* `occurredAt`
* `changedData` — optional, used for modifications
* `reason` — optional, for example, a cancellation reason

It must reference the affected booking and the responsible user. For automated events, the actor may be a system process rather than a user; the model can represent both using a nullable actor or a shared actor abstraction.

### CleaningTask

Represents a cleaning task created after check-out or requested during a stay.

* `id`
* `origin` — check-out or in-stay request
* `startedAt` — optional until work begins
* `completedAt` — optional until completion
* `notes` — optional

It must reference a room. It may reference the requesting user and the assigned housekeeper; both references are optional depending on the origin and assignment. Its lifecycle is derived from its timestamps: no `startedAt` means pending; `startedAt` without `completedAt` means in progress; and `completedAt` means completed. A completed task must also have a start timestamp, and completion must not precede the start. Store the timestamps, not a separate status value.

## Candidate enumerations

These enumerations represent closed sets. Booking status and cleaning-task lifecycle are independent; a room may have an in-progress booking while a cleaning task is in progress.

* `CleaningTaskOrigin`: `CHECK_OUT`, `IN_STAY_REQUEST`.
* `BookingStatus`: `CONFIRMED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`.
* `BookingEventType`: `CREATED`, `MODIFIED`, `CANCELLED`, `CHECKED_IN`, `CHECKED_OUT`.
* `AccountStatus`: `ACTIVE`, `INACTIVE`.

`Room.type` can be a configurable catalogue instead of an enumeration if administration must add room types without changing the code. Notes, incidents, and reasons remain free text.

## Candidate relationships and cardinalities

* **User — subtypes:** `ReceptionUser`, `HousekeepingUser`, and `AdministratorUser` inherit from `User`. Every account in this version must belong to exactly one of these subtypes.
* **Guest — Booking:** a guest may have zero or many bookings; each booking belongs to one guest.
* **Room — Booking:** a room may be associated with zero or many bookings over time; each booking is for one room.
* **User — Booking (creator):** a user may create zero or many bookings; each booking retains its creator.
* **Booking — BookingEvent:** a booking may have zero or many events; each event belongs to one booking.
* **User — BookingEvent (actor):** a user may perform zero or many events; each event identifies a user or a system process as the actor.
* **Room — CleaningTask:** a room may have zero or many tasks over time; each task belongs to one room.
* **User — CleaningTask (requester):** a user may request zero or many tasks; this relationship may be optional for tasks created automatically after check-out.
* **User — CleaningTask (assignee):** an employee may be assigned zero or many tasks; a task may not have an assignee yet.

## Guests and access accounts

`Guest` is an independent domain entity associated with bookings. Guests do not need credentials for reception to create bookings on their behalf, and `Guest` does not inherit from the staff `User` account.

If guest sign-in is required for a future online booking feature, add a guest account linked optionally to `Guest` (at most one account per guest). Keep the guest's booking/contact record separate from authentication and authorization, whether the account reuses a common authentication component or is implemented separately. Do not create guest accounts or placeholder staff users in this version.

## Derived data and model-relevant rules

* The current occupancy rate is derived as occupied rooms divided by active rooms, multiplied by 100. An occupied room with an active service-cleaning task is still counted as occupied.
* The number of active rooms and active bookings are derived query results; they do not necessarily need to be duplicated as independent data.
* A booking period includes the check-in date and excludes the check-out date. Consecutive bookings may share the check-out/check-in date.
* Cancelled bookings, completed tasks, and users or rooms referenced by historical records are retained; statuses are used instead of physical deletion.
* Only one incomplete cleaning task may exist per room at a time.
* Occupancy status and cleaning tasks are independent dimensions: a room can be occupied while a service cleaning is taking place.
* Post-check-out cleaning prevents a vacant room from being ready for a new check-in until the task is completed.
* Cleaning during a stay does not change the room's occupied status or complete the booking.
* Current occupancy is derived from the in-progress booking. Check-in and check-out traceability is available from `BookingEvent`; cleaning history and task lifecycle are derived from `CleaningTask` timestamps. A separate `RoomOccupancyHistory` entity is not included.

## Open decisions and assumptions

* The interview confirms that the guest's name is recorded, but does not specify whether an identity document, phone number, or email address is required. The user stories treat these fields as optional and first and last names as required; confirm this before finalising the model.
* In the current version, only staff accounts can access the system. Guest accounts are not part of this version; if needed later, link a separate guest account to `Guest` rather than making `Guest` a staff `User` subtype.
* To represent automated actors in history records, decide whether to use a common actor entity, an optional user plus a process identifier, or an equivalent mechanism.
* A dashboard is normally calculated from rooms and bookings; it does not need its own persistent entity unless metric snapshots or history are required.
