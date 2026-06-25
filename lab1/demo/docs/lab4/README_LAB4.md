# Lab 4: Camunda Standalone BPMS

## Architecture

Camunda Platform 7 is used as a standalone BPMS service. The Spring/WildFly application does not embed the engine. It connects to Camunda through REST and works as an external task adapter for database services, transactions, JMS and EIS.

Main process:

- BPMN: `src/main/resources/bpmn/airbnb-fast-booking-lab4.bpmn`
- process key: `airbnb-fast-booking`
- process name: `Airbnb Fast Booking Lab 4`
- startable from Camunda Tasklist: yes

Camunda Forms:

- `src/main/resources/forms/start-booking.form`
- `src/main/resources/forms/search-accommodation.form`
- `src/main/resources/forms/select-accommodation.form`
- `src/main/resources/forms/confirm-booking.form`
- `src/main/resources/forms/cancel-booking.form`

## What Runs In Camunda

Camunda owns the dynamic route of the business process: start form, user tasks, gateways, timers, message wait state, external service tasks, incidents and history.

User tasks are completed in Camunda Tasklist through Camunda Forms. The demo user does not call the application REST API by hand to start the process or complete user tasks.

## What Runs In Spring

Spring keeps the existing backend responsibilities:

- JPA/service-layer transactions and Narayana/JTA boundaries;
- Spring Security and method-level authorities;
- reservation draft creation and final reservation creation;
- Artemis/JMS producer/listener;
- Jackrabbit/JCR EIS integration;
- Quartz/Spring scheduled cleanup.

Camunda service tasks are external tasks. The Spring worker polls these topics:

- `search-accommodations`
- `check-availability`
- `create-reservation-draft`
- `send-reservation-message`
- `call-eis-adapter`
- `finalize-booking`
- `cancel-expired-draft`

## JMS And Message Correlation

The BPMN process waits for message `reservation-async-processed` after the `send-reservation-message` external task.

The Spring worker sends a JMS payload to `reservation.confirmation` with:

- `draftId`
- `taskType = CREATE_RESERVATION`
- `processInstanceId`

After the JMS listener creates the final reservation and stores the PDF contract in Jackrabbit, it calls Camunda REST `/message` itself. It correlates `reservation-async-processed` to the original `processInstanceId` and passes:

- `asyncProcessingOk = true`
- `reservationCreated = true`
- `reservationId = <created reservation id>`

Because of this, no manual Insomnia request to `/engine-rest/message` is needed.

## Roles

Spring business access is still enforced by Spring Security authorities.

Camunda Tasklist assignment is separate from Spring Security. The BPMN user tasks use candidate group `ROLE_USER`. For the demo, create or use a Camunda user that belongs to `ROLE_USER`, or adjust the Camunda demo user/group mapping on the standalone engine.

Recommended Camunda demo identities:

- user `user`, group `ROLE_USER`
- user `owner`, group `ROLE_OWNER`
- user `admin`, group `ROLE_ADMIN`

Only `ROLE_USER` is required for the current fast-booking path.

## Local Startup

Start infrastructure and Camunda standalone, then deploy the BPMN and forms:

```bash
CAMUNDA_URL=http://127.0.0.1:8080/engine-rest scripts/lab4/deploy-bpmn.sh
```

Run the backend locally:

```bash
./gradlew bootRun --args='--spring.profiles.active=local,camunda'
```

Build the deployable WAR:

```bash
./gradlew clean build
```

WildFly/helios deployment notes are in `docs/lab4/DEPLOY_WILDFLY_HELIOS.md`.

## Demo Data

The Camunda forms require real database IDs:

- `userId`: ID of an existing user;
- `idPlace`: ID of an existing place.

Do not guess these values. Use one of the existing seed flows or query PostgreSQL:

```sql
select id, username from users order by id;
select id, town, name from places order by id;
```

Use the returned IDs in Tasklist forms.

## How To Defend Through Camunda Forms

1. Start Camunda standalone.
2. Deploy BPMN and forms with `scripts/lab4/deploy-bpmn.sh`.
3. Deploy or start the Spring backend on WildFly/helios.
4. Open Camunda Tasklist.
5. Start process `Airbnb Fast Booking Lab 4`.
6. Fill the Start Form:
   - `town = Moscow`
   - `arrival = 2026-08-01`
   - `departure = 2026-08-05`
   - `guestsAmount = 1`
   - `petsAmount = 0`
   - `userId = <existing DB user id>`
   - `coverLetter = Lab4 Camunda Forms test`
7. Complete `Enter search criteria` if you need to adjust the same search values.
8. Complete `Select accommodation`:
   - `idPlace = <existing DB place id>`
9. Complete `Confirm booking`:
   - `bookingConfirmed = true`
   - leave `cancelReason` empty
10. Open Cockpit and watch the token move through external tasks.
11. Wait for the JMS async step to finish automatically.
12. In Cockpit History, show that the process instance is completed and `endTime` is set.
13. In Cockpit Incidents, show that there are no incidents.

Debug REST endpoints under `/api/bpm/**` may remain for diagnostics, but they are not the user interface for the defended scenario.

Defense phrase:

> Пользовательский интерфейс реализован через Camunda Tasklist и Camunda Forms. Пользователь не вызывает REST API приложения вручную: он запускает процесс и выполняет User Task через формы. Camunda Engine управляет маршрутом BPMN-процесса. Spring backend используется как набор адаптеров для автоматических service tasks: он через external task worker выполняет операции с БД, транзакционные сервисы, JMS и EIS. JMS-интеграция не переносится внутрь Camunda напрямую, а подключается через adapter/API, как разрешено в ТЗ.
