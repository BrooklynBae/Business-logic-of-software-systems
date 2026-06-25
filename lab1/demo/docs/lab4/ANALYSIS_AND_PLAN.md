# Lab 4 Analysis And Implementation Notes

## Current Architecture

The project is a Spring Boot application prepared for WAR deployment on WildFly. Camunda is intentionally standalone, not embedded in Spring.

Relevant components:

- BPMN: `src/main/resources/bpmn/airbnb-fast-booking-lab4.bpmn`
- Camunda Forms: `src/main/resources/forms/*.form`
- Camunda adapter: `CamundaRestClient`, `CamundaProcessService`, `CamundaTaskService`, `CamundaExternalTaskWorkers`
- JMS adapter: `JmsTaskProducer`, `ReservationConfirmationJmsListener`
- transactional domain services: `ReservationDraftService`, `ReservationService`, `PlaceService`
- security: Spring Security, JAAS/XML users, method-level authorities
- deployment docs: `docs/lab4/README_LAB4.md`, `docs/lab4/DEPLOY_WILDFLY_HELIOS.md`

## Implemented Lab 4 Flow

The primary user scenario is Camunda Tasklist plus Camunda Forms:

1. User opens Camunda Tasklist.
2. User starts `Airbnb Fast Booking Lab 4`.
3. Start form stores `town`, `arrival`, `departure`, `guestsAmount`, `petsAmount`, `userId`, `coverLetter`.
4. User task `Enter search criteria` can refine the same search variables.
5. External task `search-accommodations` calls Spring place services.
6. User task `Select accommodation` stores `idPlace`.
7. External task `check-availability` calls Spring reservation services.
8. User task `Confirm booking` stores `bookingConfirmed` and optional `cancelReason`.
9. If `bookingConfirmed == true`, Spring creates a draft and sends JMS.
10. JMS listener creates the final reservation and correlates `reservation-async-processed`.
11. Camunda continues to `finalize-booking` and completes the process.

The diagnostic `/api/bpm/**` endpoints are still present for development checks, but they are not the defended UI scenario.

## Variable Contract

User-facing form variables:

- `town`
- `arrival`
- `departure`
- `guestsAmount`
- `petsAmount`
- `userId`
- `coverLetter`
- `idPlace`
- `bookingConfirmed`
- `cancelReason`

Worker/gateway variables:

- `accommodationFound`
- `accommodationCount`
- `available`
- `draftCreated`
- `draftId`
- `asyncMessageSent`
- `asyncProcessingOk`
- `reservationCreated`
- `reservationId`

`idPlace` is the primary place variable. The worker still accepts `placeId` and `selectedPlaceId` as legacy aliases, but forms use only `idPlace`.

## JMS Correlation

`send-reservation-message` now includes `processInstanceId` in `TaskMessage`. After successful JMS processing, `ReservationConfirmationJmsListener` correlates:

- message name: `reservation-async-processed`
- target: original `processInstanceId`
- variables: `asyncProcessingOk`, `reservationCreated`, `reservationId`

This removes the need for manual Insomnia message correlation.

## Deployment

`scripts/lab4/deploy-bpmn.sh` deploys the BPMN and all Camunda Forms in one deployment so Tasklist can render the start form and user task forms.
