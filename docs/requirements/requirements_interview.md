# Client Interview

**Client**: Hello, thank you for taking the time to speak with me. I’ll explain what I need. I run a small hotel with around 40 rooms, and right now we manage everything using paper, phone calls, and Excel… and it’s becoming a mess.

**Analyst**: Perfect, could you tell me how you are currently managing it and what the main issues are?

**Client**: The main issue is that bookings sometimes overlap. Someone writes down a room for certain dates, but then another person might book the same room without realizing it. We also don’t always know which rooms are clean and ready to be used again.

**Analyst**: I understand. So there are two main problems: preventing booking overlaps and managing room status. Who is currently using the system inside the hotel?

**Client**: We have three types of staff: reception, cleaning, and administration. Each one does their part, but there is no shared system, everyone works independently.

**Analyst**: Understood. That means the system will need user roles and control over what each role can see and modify. In reception, I assume they handle bookings, correct?

**Client**: Yes, reception is responsible for bookings, customer service, check-ins and check-outs. They record the guest name, dates, and assigned room.

**Analyst**: How is room availability defined? For example, if someone books from the 10th to the 12th, is the room occupied for all those days?

**Client**: Yes, it is based on full days. If someone checks in on the 10th, that room cannot be assigned to anyone else on that day.

**Analyst**: Understood. What happens when a booking is cancelled?

**Client**: The booking is removed and the room becomes available again. However, we would like to keep a record of who cancelled it and when.

**Analyst**: Perfect, that implies keeping a history of booking actions. Regarding the rooms, what information do you store for each one?

**Client**: Each room has a number, a type (single, double, suite), a price per night, and a status.

**Analyst**: What values can that status have?

**Client**: It can be available, occupied, or in cleaning.

**Analyst**: Is that status updated manually or would you like part of it to be automated?

**Client**: Right now it’s manual, but we would like some automation. For example, when a guest checks in, the room should automatically become occupied, and when they check out, it should go to cleaning.

**Analyst**: Understood, that implies event-driven status updates. Regarding the cleaning staff, what exactly do they need?

**Client**: They need to see which rooms are in cleaning, mark when they finish a room, and add notes, for example if something is missing or there is a problem.

**Analyst**: Is cleaning always done after check-out, or can it happen at other times?

**Client**: Normally after check-out, but sometimes also during a stay if there is a request or an issue.

**Analyst**: Understood. So cleaning can be triggered by scheduled events as well as incidents. Regarding administration, what are their responsibilities?

**Client**: Administration has full control of the system: managing users, rooms, bookings, and overall hotel configuration.

**Analyst**: Perfect, so administration acts as a full-access role with permissions over the entire system.

**Client**: Yes, exactly.

**Analyst**: Besides that, would you like any kind of global overview of the hotel?

**Client**: Yes, we would like a general dashboard showing how many rooms are available, how many are occupied, and the overall occupancy rate.

**Analyst**: Understood, that would be a management dashboard with key metrics.

**Client**: Yes, something visual and easy to understand.

**Analyst**: Got it. Will the system be used only internally, or will guests be able to book rooms directly in the future?

**Client**: For now only internally. In the future we might allow online bookings, but it is not required at the moment.

**Analyst**: Perfect, but we will design it in a way that allows future expansion without major changes. Will every employee have their own user account?

**Client**: Yes, each employee should have their own login. Not everyone should be able to do everything.

**Analyst**: Understood. So we need role-based access control: reception, cleaning, and administration, each with different permissions. Administration will have full access, reception will manage bookings, and cleaning will manage room status.

**Client**: Yes, that is correct.

**Analyst**: Perfect. To confirm, I will summarize what I have understood:

The system must manage rooms with number, type, price, and status. The status can be available, occupied, or in cleaning, with automatic updates based on events such as check-in and check-out.

Bookings will include guest name, check-in and check-out dates, and assigned room, preventing date overlaps. A history of booking actions, including cancellations, must be stored.

There will be user roles: reception, cleaning, and administration, with administration having full access to the system.

The cleaning module will allow staff to view rooms pending cleaning, mark them as completed, and add notes.

A dashboard will provide occupancy metrics and general hotel overview.

The system should allow future expansion to support external online bookings.

**Client**: Yes, that is all correct.

**Analyst**: Perfect, with this we can define the use cases and the system model to start the design.
