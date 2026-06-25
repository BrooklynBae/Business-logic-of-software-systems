# Defense Notes: Lab 4 Camunda Forms Scenario

## Main Claim

The demo flow is no longer driven through Insomnia or custom REST calls. The user starts and completes the booking process in Camunda Tasklist with Camunda Forms.

Spring still talks to Camunda REST because Camunda is standalone and the backend is an adapter for external tasks, JMS, transactions and EIS.

## Process

- key: `airbnb-fast-booking`
- name: `Airbnb Fast Booking Lab 4`
- BPMN file: `src/main/resources/bpmn/airbnb-fast-booking-lab4.bpmn`
- forms directory: `src/main/resources/forms`
- start form: `start-booking.form`

The BPMN is startable in Tasklist and has a Camunda Form attached to the start event.

## User Forms

Start booking:

- `town`
- `arrival`
- `departure`
- `guestsAmount`
- `petsAmount`
- `userId`
- `coverLetter`

Enter search criteria:

- `town`
- `arrival`
- `departure`
- `guestsAmount`
- `petsAmount`
- `coverLetter`

Select accommodation:

- `idPlace`

Confirm booking:

- `bookingConfirmed`
- `cancelReason`

Cancel booking:

- `cancelReason`
- `bookingCanceled`

The form field names match Java process variables read by workers and BPMN gateways.

## Automatic Steps

Spring external workers handle:

- `search-accommodations`
- `check-availability`
- `create-reservation-draft`
- `send-reservation-message`
- `call-eis-adapter`
- `finalize-booking`
- `cancel-expired-draft`

Transactions stay in Spring service methods and JMS listeners. Camunda controls the order of execution and incidents, not distributed transactions.

## JMS

The `send-reservation-message` worker sends a JMS message with `draftId`, `taskType` and `processInstanceId`.

The `ReservationConfirmationJmsListener` creates the final reservation, removes the draft, writes the PDF contract to Jackrabbit, then correlates Camunda message `reservation-async-processed` back to the waiting process instance.

Variables correlated to Camunda:

- `asyncProcessingOk = true`
- `reservationCreated = true`
- `reservationId = <created reservation id>`

Manual message correlation from Insomnia is not part of the demo.

## What To Show

1. Camunda standalone is running.
2. Deployment contains BPMN and all `.form` files.
3. Tasklist shows `Airbnb Fast Booking Lab 4` in Start process.
4. Start form is displayed and saves variables.
5. User tasks open their Camunda Forms.
6. Cockpit shows external tasks being completed by the Spring worker.
7. Artemis/JMS receives `reservation.confirmation`.
8. The JMS listener correlates `reservation-async-processed`.
9. History shows completed process instance with non-empty `endTime`.
10. Incidents list is empty.

## Demo Values

Use real IDs from PostgreSQL:

```sql
select id, username from users order by id;
select id, town, name from places order by id;
```

Example form values:

- `town = Moscow`
- `arrival = 2026-08-01`
- `departure = 2026-08-05`
- `guestsAmount = 1`
- `petsAmount = 0`
- `userId = <existing DB user id>`
- `coverLetter = Lab4 Camunda Forms test`
- `idPlace = <existing DB place id>`
- `bookingConfirmed = true`
