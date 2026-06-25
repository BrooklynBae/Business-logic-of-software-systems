# Анализ проекта и план Lab4 Camunda

## Что было прочитано

Для восстановления реального процесса использованы:

- `tests7.json` как старый Insomnia-сценарий лабораторной;
- контроллеры `UserController`, `OwnerController`, `PlaceController`, `ReservationController`, `PaymentController`, `BpmReservationController`;
- сервисы `UserService`, `OwnerService`, `PlaceService`, `ReservationDraftService`, `ReservationService`, `PaymentService`, `SystemReservationService`;
- JMS listeners/producers `JmsTaskProducer`, `MailSendingJmsListener`, `ReservationConfirmationJmsListener`;
- entities `User`, `Owner`, `Place`, `ReservationDraft`, `Reservation`, `PaymentType`, `PaymentMethod`;
- repositories и security-код `SecurityConfig`, `AppSecurityExpressions`, JAAS/XML registry;
- Camunda adapter classes `CamundaRestClient`, `CamundaExternalTaskWorkers`, `CamundaVariablesMapper`, `CamundaProcessConstants`;
- BPMN/forms из `src/main/resources/bpmn` и `src/main/resources/forms`.

## Реальный бизнес-процесс из кода

В проекте нет отдельного enum статуса для черновика. Состояние заявки хранится в `ReservationDraft.coverLetter` через префиксы:

- `[PENDING_ADMIN]` - заявка создана и ждёт модерации администратора;
- `[APPROVED_BY_ADMIN]` - админ одобрил текст, письмо владельцу ещё отправляется;
- `[EMAIL_SENT_TO_OWNER]` - письмо владельцу отправлено, ждём решения владельца;
- `[APPROVED_BY_OWNER]` - владелец одобрил заявку, можно оплачивать;
- `[REJECTED]` - отклонено администратором;
- `[REJECTED_BY_OWNER]` - отклонено владельцем.

Старый Insomnia workflow из `tests7.json`:

1. Создать профиль USER.
2. Создать профиль OWNER.
3. Войти USER/OWNER/ADMIN.
4. OWNER создаёт жильё.
5. USER создаёт draft бронирования.
6. Оплата до модерации админом должна падать.
7. ADMIN модерирует draft.
8. Повторная модерация должна падать.
9. Оплата до подтверждения владельцем должна падать.
10. OWNER подтверждает draft.
11. USER оплачивает.
12. Создаётся финальная reservation.
13. Генерируется PDF-договор в Jackrabbit/JCR.

## Что перенесено в BPMN

Подготовка профилей и жилья остаётся seed/demo-data шагом backend. Сам процесс бронирования теперь управляется Camunda:

1. `[USER] Start booking`
2. `[USER] Enter search criteria`
3. `[SYSTEM] Search accommodations`
4. `[USER] Select accommodation`
5. `[SYSTEM] Check availability`
6. `[USER] Confirm booking request`
7. `[SYSTEM] Create reservation draft`
8. `[ADMIN] Moderate reservation message`
9. `[SYSTEM] Apply admin moderation`
10. `[OWNER] Approve booking`
11. `[SYSTEM] Apply owner decision`
12. `[USER] Pay reservation`
13. `[SYSTEM] Process payment`
14. `[SYSTEM] Call EIS adapter`
15. `[JMS] Send reservation confirmation message`
16. `[JMS] reservation-async-processed`
17. `[SYSTEM] Finalize booking`
18. `[SYSTEM] Generate contract/report`
19. `Booking completed`

## Почему это не новый сценарий

BPMN вызывает существующую бизнес-логику:

- поиск жилья: `PlaceService`;
- проверка дат: `ReservationService.ensureDatesAvailable`;
- draft: `ReservationDraftService.createDraft`;
- admin moderation: `ReservationDraftService.moderateByAdminFromProcess`;
- owner decision: `ReservationDraftService.confirmByOwnerFromProcess`;
- payment validation: `PaymentService.preparePaymentFromProcess`;
- final reservation: `ReservationConfirmationJmsListener -> ReservationService.confirmReservation`;
- PDF: `ReservationConfirmationJmsListener -> Jackrabbit/JCR`.

REST endpoints старого сценария оставлены для диагностики и обратной совместимости, но защита Lab4 выполняется через Camunda Tasklist и Cockpit.

## Новые/используемые topics

- `search-accommodations`
- `check-availability`
- `create-reservation-draft`
- `moderate-reservation`
- `owner-confirm-reservation`
- `process-payment`
- `call-eis-adapter`
- `send-reservation-message`
- `finalize-booking`
- `generate-contract-report`
- `cancel-expired-draft`

## Переменные процесса

Пользовательские и доменные:

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
- `draftId`
- `adminApproved`
- `adminComment`
- `ownerApproved`
- `ownerComment`
- `paymentConfirmed`
- `paymentType`
- `paymentMethod`
- `reservationId`
- `asyncProcessingOk`
- `reservationCreated`
- `contractGenerated`
- `contractId`
- `contractPdfUrl`

## Важное ограничение

Camunda Tasklist users/groups не равны Spring Security/JWT users. Spring Security защищает backend REST и сервисы. Camunda candidate groups управляют human tasks внутри Tasklist.
