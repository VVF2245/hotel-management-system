# User Stories

## Access and administration

### User Story — US-01: Sign in

As a hotel employee,<br>
I want to sign in with my username and password,<br>
so that I can access the features allowed by my role.

### Acceptance Criteria

- Scenario 1: Valid credentials. When a user enters the correct username and password for an active account, the system grants access.
- Scenario 2: Invalid credentials. If the username or password is incorrect, the system denies access and displays an error without revealing which value was correct.
- Scenario 3: Inactive account. If the account is inactive, the system denies access.
- Scenario 4: First sign-in. A newly created user receives a temporary password from the administrator and must choose a new password before using the system.
- Scenario 5: Forgotten password. An administrator can issue a new temporary password; the user must change it at the next sign-in.
- Data model: A user has first and last name, a unique username, email address, securely hashed password, user type, account status, and creation date.

### Technical Constraints

- Passwords, including temporary passwords, must not be stored or logged in plaintext. Use a password-hashing algorithm. A temporary password must be changed at first sign-in; no password length or composition policy is specified because none has been agreed.

### User Story — US-02: Manage users

As an administrator,<br>
I want to create and update users, assign them a user type, and deactivate their accounts,<br>
so that I can manage employee access to the system.

### Acceptance Criteria

- Scenario 1: Create and update. Administration can create a user and update their details and user type.
- Scenario 2: Deactivation. Administration can deactivate an account, and the account can no longer be used to sign in.
- Scenario 3: Traceability. If a user has associated activity, their record is retained and the account is deactivated instead of being physically deleted.
- Scenario 4: Initial access. When creating a user, administration sets a temporary password and gives it to the employee through an appropriate channel. The user must replace it at first sign-in.
- Data model: User types are Reception, Housekeeping, and Administration; account status is Active or Inactive.

### User Story — US-03: Manage rooms

As an administrator,<br>
I want to add, update, and deactivate rooms,<br>
so that I can keep the hotel's room inventory up to date.

### Acceptance Criteria

- Scenario 1: Add a room. Administration can register a room with a number, type, and nightly price.
- Scenario 2: Deactivation with history. A room linked to historical bookings or tasks is retained and marked inactive instead of being physically deleted.
- Scenario 3: Active or scheduled use. The system prevents a room from being deactivated while it has an in-progress stay, a confirmed booking, or an incomplete cleaning task. Confirmed bookings must first be cancelled or reassigned to another room.
- Data model: A room has a unique number, type, nightly price, optional notes, and an active/inactive record status. Active cleaning is represented by its task and may coexist with an occupied room.

### User Story — US-04: View rooms

As a hotel employee,<br>
I want to view active rooms, their current occupancy, and their availability for a date range <br>
so that I know which rooms can be assigned or prepared.

### Acceptance Criteria

- Scenario 1: General view. The system displays active rooms, whether each is vacant or occupied, and whether it has an active cleaning task. A room may be occupied and undergoing cleaning at the same time.
- Scenario 2: Date-range availability. When dates are provided, the system reports availability based on bookings that apply to that period.
- Data model: Availability for a date range is derived from bookings and is not stored as a separate room status.

## Guests and bookings

### User Story — US-05: Manage guests

As a receptionist,<br>
I want to create and update guest details,<br>
so that I can identify guests and associate them with their bookings.

### Acceptance Criteria

- Scenario 1: Create a guest. Reception can register a guest with first and last name.
- Scenario 2: Optional contact details. An identity document, phone number, and email address may be recorded when provided.
- Data model: A guest has required first and last names; identity document, phone number, and email address are optional.

### User Story — US-06: Create a booking

As a receptionist,<br>
I want to create a booking by specifying the guest, room, and dates,<br>
so that I can organize the guest's stay.

### Acceptance Criteria

- Scenario 1: Complete details. A booking can be confirmed when it has a guest, an active room, and valid dates.
- Scenario 2: Missing details or invalid dates. The system does not confirm the booking and explains what needs to be corrected.
- Data model: A booking includes a guest, room, check-in date, check-out date, status, creation date, and the user who created it.

### User Story — US-07: Prevent conflicting bookings

As a receptionist,<br>
I want the system to check room availability before confirming or updating a booking,<br>
so that the same room is not assigned to different guests for overlapping periods.

### Acceptance Criteria

- Scenario 1: Dates are available. The system confirms the booking if no other confirmed or in-progress booking overlaps for that room.
- Scenario 2: Dates are unavailable. The system rejects the operation if an incompatible booking exists.
- Scenario 3: Cancelled booking. A cancelled booking does not block those dates.
- Scenario 4: Simultaneous requests. If two concurrent requests attempt to book incompatible dates, at most one can be confirmed.

### Technical Constraints

- Date and availability validation must run on the server. Availability checks and booking confirmation must preserve data integrity under concurrent operations.

### User Story — US-08: Update a booking

As a receptionist,<br>
I want to update the details of an existing booking,<br>
so that I can correct or change the stay when necessary.

### Acceptance Criteria

- Scenario 1: Confirmed booking. Reception can update the guest, room, or dates of a confirmed booking. The selected room must be active, and its dates must remain valid and available.
- Scenario 2: In-progress booking. After check-in, reception can change only the check-out date. The guest, room, and check-in date cannot be changed; changing rooms during a stay is outside the scope of this version.
- Scenario 3: Unmodifiable booking. Cancelled and completed bookings cannot be updated.
- Scenario 4: Revalidation. The system validates dates, room eligibility, and availability on the server before saving changes.
- Scenario 5: Audit history. Every update is recorded with the changed data, responsible user, and date and time.

### User Story — US-09: Cancel a booking

As a receptionist,<br>
I want to cancel a confirmed booking,<br>
so that its dates are released and the cancellation is recorded.

### Acceptance Criteria

- Scenario 1: Cancellation. A confirmed booking can be cancelled and stops blocking its dates.
- Scenario 2: Retention. The booking remains stored with Cancelled status; it is not physically deleted.
- Scenario 3: Audit record. The affected booking, the user who cancelled it, the date and time, and an optional reason are retained.

### User Story — US-10: Search bookings

As a receptionist,<br>
I want to view and filter bookings by guest, room, status, and dates,<br>
so that I can quickly find the details of a stay.

### Acceptance Criteria

- Scenario 1: Filtering. The list displays bookings matching the selected filters.
- Scenario 2: Booking details. Each result provides the guest, room, dates, and booking status.

### User Story — US-11: View booking and cleaning history

As an administrator,<br>
I want to review booking actions and cleaning tasks,<br>
so that I can trace activity in the hotel.

### Acceptance Criteria

- Scenario 1: Booking events. The history includes creation, modification, cancellation, check-in, and check-out, with the responsible user and date and time.
- Scenario 2: Occupancy and cleaning. Current occupancy is derived from the in-progress booking, and cleaning history is available from cleaning tasks.
- Scenario 3: Changed data. For a booking update, the history identifies which data changed.
- Scenario 4: Historical integrity. Users cannot modify historical events.
- Data model: `BookingEvent` references the booking and stores the event type, user, date and time, changed data when applicable, and an optional cancellation reason. `CleaningTask` retains the history of completed cleaning work.

## Stays and housekeeping

### User Story — US-12: Record check-in

As a receptionist,<br>
I want to record check-in for a confirmed booking,<br>
so that the stay begins and the room's occupancy can be determined.

### Acceptance Criteria

- Scenario 1: Valid check-in. The booking changes to In Progress.
- Scenario 2: Room not ready. Check-in is not allowed if the room is already occupied or has a post-check-out cleaning task that has not been completed. Service cleaning during another stay does not make the room vacant.
- Scenario 3: Traceability. The check-in is recorded in the booking history.

### User Story — US-13: Record check-out

As a receptionist,<br>
I want to record check-out for an in-progress booking,<br>
so that the stay ends and the post-stay cleaning process begins.

### Acceptance Criteria

- Scenario 1: Valid check-out. The booking changes to Completed; the room is no longer occupied, but is not ready for another stay until its post-check-out cleaning task is completed.
- Scenario 2: Cleaning task. The system creates a pending cleaning task linked to the room and check-out.
- Scenario 3: Traceability. The check-out is recorded in the booking history.

### User Story — US-14: Request cleaning during a stay

As a receptionist or administrator,<br>
I want to create a cleaning task during a stay,<br>
so that I can address a request or issue without changing the booking.

### Acceptance Criteria

- Scenario 1: Create a task. The task is associated with the room, identifies an in-stay request as its origin, and can be assigned to a housekeeper.
- Scenario 2: Room occupancy. Creating or completing this task does not change the room's occupied condition or complete the booking.
- Scenario 3: Concurrent tasks. A room cannot have more than one incomplete cleaning task at a time.
- Data model: A task includes a room, origin, optional requesting user, optional assigned housekeeper, start and completion timestamps, and notes or incidents. It does not store a separate status: no start timestamp means Pending; a start timestamp without a completion timestamp means In Progress; a completion timestamp means Completed.

### User Story — US-15: Manage cleaning tasks

As a housekeeper,<br>
I want to view, start, and complete cleaning tasks and add notes,<br>
so that I can organize my work and report issues.

### Acceptance Criteria

- Scenario 1: View tasks. Housekeeping staff can view pending and in-progress tasks.
- Scenario 2: Update tasks. They can start a task, add notes or report incidents, and record its completion.
- Scenario 3: Retain task history. Start and completion timestamps are retained; the system derives whether a task is pending, in progress, or completed. Completed tasks remain available as history.
- Scenario 4: Post-check-out cleaning. When the post-check-out cleaning task is completed, the room is ready for a new stay unless another post-check-out task remains incomplete.
- Scenario 5: In-stay cleaning. Completing a task requested during a stay does not change the room's occupied condition.

### User Story — US-16: View the dashboard

As an administrator,<br>
I want to view a visual summary of the hotel's current status,<br>
so that I can understand occupancy and booking activity.

### Acceptance Criteria

- Scenario 1: Current room information. The dashboard shows rooms ready for a stay, occupied rooms, and rooms with an active cleaning task. The occupied and cleaning counts may overlap.
- Scenario 2: Occupancy rate. The dashboard shows the current rate, calculated as occupied rooms divided by active rooms, multiplied by 100. A room occupied during service cleaning still counts as occupied.
- Scenario 3: Active bookings. The dashboard shows the number of Confirmed or In Progress bookings. A future confirmed booking counts as active but not as an occupied room in the current occupancy rate.
- Scenario 4: Current figures. Dashboard metrics reflect the hotel's status at the time of the query.

## System administration and use

### User Story — US-17: Use a clear interface

As a hotel employee,<br>
I want to use a clear interface and receive understandable messages,<br>
so that I can complete my work without advanced technical training.

### Acceptance Criteria

- Scenario 1: Validation feedback. When an action cannot be completed, the message explains which data or rule needs to be corrected.
- Scenario 2: Daily operation. The main tasks can be completed through the interface without direct access to the database.
- Scenario 3: Date format. Dates are displayed and entered using the en-GB convention dd/MM/yyyy. Date values are stored and exchanged in an unambiguous ISO 8601-compatible format, independently of their display format.

### User Story — US-18: Restrict access by user type

As an administrator,<br>
I want each user type to be limited to its authorized actions,<br>
so that information is protected and staff responsibilities are separated.

### Acceptance Criteria

- Scenario 1: Reception. Reception can manage guests and bookings, record check-in and check-out, and request cleaning.
- Scenario 2: Housekeeping. Housekeeping can view and update cleaning tasks, but cannot manage bookings, users, or administrative data.
- Scenario 3: Administration. Administration can manage users, rooms, and bookings, and view the dashboard.
- Scenario 4: Unauthorized access. An operation not allowed for a user type is denied even if invoked outside the user interface.

### Technical Constraints

- Authorization checks must be enforced on the server, not only by hiding options in the interface.

## Out of scope and future considerations

Guest-facing online booking is not part of this version; the system is for internal staff use. It may be considered later, but it is not a user story or delivery commitment for the current release.

Keep booking rules and core reservation operations independent of the staff-facing interface so that a future booking channel can reuse them. Any future channel must use the same server-side validation and availability rules. Do not implement guest accounts, a public booking interface, or channel-specific behavior until those requirements are defined.

## Business Rules

### BR-01. Booking period interpretation

The check-in date is inclusive and the check-out date is exclusive. The check-out date must be later than the check-in date. Therefore, a booking from the 10th to the 12th and another from the 12th to the 14th do not overlap; both can be confirmed if all other conditions are met.

### BR-02. Booking overlaps

Two bookings for the same room overlap when the check-in date of one is earlier than the check-out date of the other and the check-in date of the other is earlier than the check-out date of the first. Confirmed and in-progress bookings block overlapping dates. Cancelled and completed bookings do not block future availability.

### BR-03. Future availability and operational readiness

Availability for a date range is determined by the absence of overlapping confirmed or in-progress bookings. Current occupancy alone does not block a future booking. To check in, a room must be vacant and ready: it must not have another in-progress stay or a pending or in-progress post-check-out cleaning task. Service cleaning during a stay may take place while the room is occupied.

### BR-04. Occupancy and cleaning

An active room has exactly one occupancy condition: vacant or occupied. Cleaning tasks have their own lifecycle and may be active while the room is occupied. Occupancy and cleaning are therefore not mutually exclusive. A room is ready for a stay when it is vacant and has no pending or in-progress post-check-out cleaning task. These current conditions do not replace booking checks for future dates.

### BR-05. Stay transitions

A valid check-in changes the room's occupancy condition from vacant to occupied. A valid check-out changes it from occupied to vacant and creates a post-stay cleaning task. The room is not ready for another stay until that task is completed.

### BR-06. Readiness after check-out

A room with a pending or in-progress post-check-out cleaning task is not ready for a new check-in. Once that task is completed, the room is ready if no other post-check-out cleaning task is pending or in progress. Service-cleaning tasks during a stay do not make an occupied room vacant.

### BR-07. Cleaning during a stay

A cleaning task requested during a stay does not end or change the booking or the room's occupied condition. A room can therefore be occupied and undergoing cleaning at the same time. Only one incomplete cleaning task may exist for a room at a time.

### BR-08. Record retention

Cancelled bookings, completed cleaning tasks, and users or rooms referenced by historical records are retained for traceability. Bookings retain their Cancelled status, and users and rooms may be marked inactive; cleaning-task completion is derived from its start and completion timestamps rather than a stored status.

### BR-09. Cancellations

Every cancellation records the affected booking, the responsible user, and the date and time. The reason is optional.

### BR-10. Required booking data

A booking must have a guest, an active room, a check-in date, and a valid check-out date before it can be confirmed.

### BR-11. Dashboard metrics

The dashboard reflects the hotel's status at the time of the query. The current occupancy rate is calculated as occupied rooms / active rooms × 100. Active bookings are those that are Confirmed or In Progress; a future confirmed booking counts as active but not as an occupied room in the current occupancy metric.

### BR-12. Room deactivation

A room cannot be deactivated while it has an in-progress stay, any confirmed booking, or an incomplete cleaning task. Confirmed bookings must first be cancelled or reassigned to another active room. Historical bookings and completed cleaning tasks are retained when a room is deactivated.

### BR-13. Booking updates

A confirmed booking may be updated, subject to the room and date validation rules. Once check-in has been recorded, only the check-out date may be changed, and it must remain valid and not conflict with another booking. The guest, room, and check-in date cannot be changed after check-in. Cancelled and completed bookings cannot be updated. Every permitted update is recorded in the booking history.

### BR-14. Date representation

The interface is in English and displays and accepts dates using the en-GB convention dd/MM/yyyy. The application stores and exchanges date values in an unambiguous ISO 8601-compatible representation (for example, yyyy-MM-dd for a date without a time); display formatting does not change date interpretation.
