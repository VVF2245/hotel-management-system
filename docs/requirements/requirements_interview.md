# Client Interview

**Client**: Hello, thank you for taking the time to speak with me. I’ll explain what I need. I run a small hotel with around 40 rooms, and right now we manage everything using paper, phone calls, and Excel… and it’s becoming a mess.

**Analyst**: Perfect, could you tell me how you are currently managing it and what the main issues are?

**Client**: The main issue is that bookings sometimes overlap. Someone writes down a room for certain dates, but then another person might book the same room without realizing it. We also don’t always know which rooms are clean and ready to be used again.

**Analyst**: I understand. So there are two main problems: preventing booking overlaps and managing room status. Who is currently using the system inside the hotel?

**Client**: We have three types of staff: reception, cleaning, and administration. Each one does their part, but there is no shared system, everyone works independently.

**Analyst**: Understood. That means the system will need user roles and control over what each role can see and modify. In reception, I assume they handle bookings, correct?

**Client**: Yes, reception is responsible for bookings, customer service, check-ins and check-outs. They record the guest name, dates, and assigned room.

**Analyst**: How is room availability defined? For example, if someone checks in on the 10th and checks out on the 12th, can another guest check in on the 12th?

**Client**: Yes. The room is reserved for the nights of the 10th and 11th. The check-out date is not included, so another guest can check in on the 12th if the room is ready.

**Analyst**: What language and date format should staff see in the application?

**Client**: The interface should be in English. Use the dd/MM/yyyy date format.

**Analyst**: Understood. What happens when a booking is cancelled?

**Client**: It should stop blocking the dates, but not be deleted. We want to keep a record of who cancelled it and when.

**Analyst**: Perfect, that implies keeping a history of booking actions. Regarding the rooms, what information do you store for each one?

**Client**: Each room has a number, a type (single, double, suite), a price per night, and a status.

**Analyst**: Can a room have more than one operational condition at the same time, for example, be occupied while a requested cleaning is in progress?

**Client**: Yes. We need to know separately whether it is occupied and whether it has a cleaning task. A room can be occupied and have a cleaning task during the stay.

**Analyst**: Which rooms can be deactivated from the hotel's active inventory?

**Client**: A room should not be deactivated while a guest is staying there, while it has a confirmed booking for a future date, or while a cleaning task is unfinished. We would first cancel or move any confirmed bookings to another room.

**Analyst**: Is occupancy updated manually or should it follow the stay?

**Client**: It should follow the booking: check-in makes the room occupied, and check-out makes it vacant and creates a cleaning task. A room is ready for a new stay only after post-check-out cleaning is complete.

**Analyst**: Regarding the cleaning staff, what exactly do they need?

**Client**: They need to see which rooms are in cleaning, mark when they finish a room, and add notes, for example if something is missing or there is a problem.

**Analyst**: Is cleaning always done after check-out, or can it happen at other times?

**Client**: Normally after check-out, but sometimes also during a stay if there is a request or an issue.

**Analyst**: Understood. So cleaning can be triggered by scheduled events as well as incidents. Regarding administration, what are their responsibilities?

**Client**: Administration has full control of the system: managing users, rooms, and bookings, and viewing the dashboard.

**Analyst**: Perfect, so administration acts as a full-access role with permissions over the entire system.

**Client**: Yes, exactly.

**Analyst**: Besides that, would you like any kind of global overview of the hotel?

**Client**: Yes, we would like a general dashboard showing how many rooms are available, how many are occupied, and the overall occupancy rate.

**Analyst**: Understood, that would be a management dashboard with key metrics.

**Client**: Yes, something visual and easy to understand.

**Analyst**: Got it. Will the system be used only internally, or will guests be able to book rooms directly in the future?

**Client**: For now it is only for staff. Online bookings might be useful in the future, but they are not required for this version.

**Analyst**: Understood. We will keep the booking rules separate from the staff interface so they can be reused if another booking channel is defined later. We will not build guest-facing booking or guest accounts in this version. Will every employee have their own user account?

**Client**: Yes, each employee should have their own login. Not everyone should be able to do everything.

**Analyst**: How should employees get access when an administrator creates their account?

**Client**: The administrator can give them a temporary password through an appropriate channel. Employees must choose a new password the first time they sign in. If they forget it, administration can issue another temporary password.

**Analyst**: Understood. So we need role-based access control: reception, cleaning, and administration, each with different permissions. Administration will have full access, reception will manage bookings, and cleaning will manage cleaning tasks.

**Client**: Yes, that is correct.

**Analyst**: Can reception change a booking after the guest has checked in?

**Client**: They may need to adjust the check-out date, but they should not change the guest, room, or check-in date after the stay has started. Any allowed change must be checked against other bookings and recorded in the history. Changing rooms during a stay is not needed for this version.

**Analyst**: And before check-in?

**Client**: A confirmed booking can be changed, as long as the dates and room are valid and do not conflict with another booking.

**Analyst**: Perfect. To confirm, I will summarize what I have understood:

The system must manage rooms with number, type, price, and active/inactive record status. Current occupancy and cleaning tasks are tracked separately; a room can be occupied and have an in-stay cleaning task at the same time. Check-in and check-out update occupancy, and check-out creates a post-stay cleaning task. A room cannot be deactivated while it has an in-progress stay, a confirmed booking, or an incomplete cleaning task.

The staff interface will be in English, with dates displayed as dd/MM/yyyy. Booking date values are stored and exchanged in an unambiguous ISO 8601-compatible format. Bookings will include guest name, check-in and check-out dates, and assigned room, preventing date overlaps. Check-in is inclusive and check-out is exclusive; for example, a booking from 10/05/2026 to 12/05/2026 does not overlap one starting on 12/05/2026. A history of booking actions, including cancellations, must be stored. Confirmed bookings can be updated before check-in; after check-in, only the check-out date can be changed.

There will be user roles: reception, cleaning, and administration, with administration having full access to the system. New users receive a temporary password and must replace it on first sign-in.

The cleaning module will allow staff to view rooms pending cleaning, mark them as completed, and add notes.

A dashboard will provide occupancy metrics and general hotel overview.

Guest-facing online bookings are a possible future enhancement, not part of this version.

**Client**: Yes, that is all correct.

**Analyst**: Perfect, with this we can define the use cases and the system model to start the design.
