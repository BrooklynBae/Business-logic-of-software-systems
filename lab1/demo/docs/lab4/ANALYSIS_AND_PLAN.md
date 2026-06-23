# Анализ проекта и план реализации ЛР4

## Что уже есть в проекте

Проект представляет собой Java 17 / Spring Boot приложение на Gradle в пакете
`com.blps_lab1.demo`. Главный класс приложения: `DemoApplication`. Также уже есть
`ServletInitializer`, поэтому проект можно готовить к WAR-развёртыванию под WildFly,
не ломая локальный запуск через Spring Boot.

В проекте уже реализованы компоненты ЛР1-ЛР3:

- REST-контроллеры: `AuthController`, `UserController`, `OwnerController`,
  `PlaceController`, `ReservationController`, `PaymentController`,
  `ServiceOptionController`.
- Бизнес-сервисы: `PlaceService`, `ReservationDraftService`, `ReservationService`,
  `PaymentService`, `SystemReservationService`, а также сервисы пользователей,
  владельцев и дополнительных услуг.
- Сущности и репозитории: `Place`, `Owner`, `User`, `ReservationDraft`,
  `Reservation`, `ServiceOption`, `PaymentMethod`, `PaymentType` и Spring Data
  repositories.
- Безопасность: Spring Security, JWT-фильтр, JAAS login module, XML-файл
  пользователей `users.xml`.
- Роли и права: `ROLE_ADMIN`, `ROLE_OWNER`, `ROLE_USER`,
  `PERM_PROCESS_PAYMENT`, `PERM_MANAGE_OWN_PLACES`, `PERM_MANAGE_USERS`,
  `PERM_MODERATE_DRAFTS`, `PERM_CONFIRM_RESERVATIONS`.
- Транзакции: сервисы используют Spring `@Transactional`, JMS-listener'ы работают
  через настроенный transaction manager.
- JMS/Artemis: `JmsTaskProducer`, `MailSendingJmsListener`,
  `ReservationConfirmationJmsListener`, `XaTransactionConfig`.
- Quartz: `DeleteExpiredDraftsJob`, который запускается через `QuartzConfig`
  каждые 10 минут.
- Интеграция с внешней системой: `JackrabbitJcaConfig` создаёт JCR-репозиторий,
  а listener подтверждения бронирования сохраняет PDF-договор в Jackrabbit.
- Тесты и коллекции: существующие JSON-export'ы в корне проекта и файлы в
  `rest-tests/`.

Файл `application.properties` был добавлен в `src/main/resources` на основе переданного
пользователем файла. Старые настройки сохранены, новые настройки Camunda добавлены в
конец файла.

## Где находится текущая бизнес-логика

Сценарий быстрого бронирования сейчас оркестрируется статически внутри Spring-сервисов:

- поиск жилья: `PlaceService.findByTown`, `PlaceService.findAllSortedByRating`;
- проверка доступности дат: `ReservationService.ensureDatesAvailable`;
- создание черновика бронирования: `ReservationDraftService.createDraft`;
- модерация администратором: `ReservationDraftService.moderateByAdmin`;
- подтверждение владельцем: `ReservationDraftService.confirmByOwner`;
- асинхронное подтверждение бронирования: `JmsTaskProducer` отправляет сообщение в
  `reservation.confirmation`, а `ReservationConfirmationJmsListener` создаёт итоговую
  бронь и сохраняет PDF в EIS;
- отправка письма и обновление статуса: очередь `mail.sending` и
  `MailSendingJmsListener`;
- очистка просроченных черновиков: `ReservationDraftService.deleteExpiredDrafts`,
  вызываемый из Quartz job.

Эта логика не удаляется. В ЛР4 поверх неё добавляется слой BPM-оркестрации через
standalone Camunda.

## Какие сервисы оборачиваются в Camunda external tasks

Camunda external task topics вызывают существующие Spring-сервисы и адаптеры:

- `search-accommodations` -> `PlaceService.findByTown` или
  `PlaceService.findAllSortedByRating`;
- `check-availability` -> `ReservationService.ensureDatesAvailable`;
- `create-reservation-draft` -> `ReservationDraftService.createDraft`;
- `send-reservation-message` -> `JmsTaskProducer.sendToQueue`;
- `call-eis-adapter` -> проверка/использование существующего Jackrabbit repository;
- `finalize-booking` -> финальный шаг процесса, при этом существующий JMS-сценарий
  подтверждения сохраняется;
- `cancel-expired-draft` -> `ReservationDraftService.deleteExpiredDrafts`.

Распределённые транзакции не переносятся в Camunda. Camunda только координирует процесс.

## Какие REST endpoints сохраняются

Все старые endpoints остаются:

- `/auth/**`
- `/user/**`
- `/owners/**`
- `/places/**`
- `/reservation/**`
- `/payment/**`
- `/service-options/**`

Они нужны для старых тестов, сценариев ЛР1-ЛР3 и прямой проверки доменной логики.

## Какие REST endpoints добавляются

Для ЛР4 добавляются endpoints:

- `POST /api/bpm/reservations/start`
- `GET /api/bpm/reservations/{processInstanceId}`
- `GET /api/bpm/tasks/my`
- `POST /api/bpm/tasks/{taskId}/complete`
- `POST /api/bpm/reservations/{processInstanceId}/cancel`

Они обращаются к standalone Camunda через REST API и остаются защищёнными Spring
Security/JWT.

## Как выглядит BPMN-процесс

BPMN-модель находится здесь:

`src/main/resources/bpmn/airbnb-fast-booking-lab4.bpmn`

Ключ процесса:

`airbnb-fast-booking`

Основной сценарий:

1. Start Event.
2. User Task: ввод критериев поиска, candidate group `ROLE_USER`.
3. Service Task external: `search-accommodations`.
4. Exclusive Gateway: найдено ли жильё.
5. User Task: выбор жилья, candidate group `ROLE_USER`.
6. Service Task external: `check-availability`.
7. Exclusive Gateway: доступно ли жильё.
8. User Task: подтверждение бронирования, candidate group `ROLE_USER`.
9. Service Task external: `create-reservation-draft`.
10. Service Task external: `call-eis-adapter`.
11. Service Task external: `send-reservation-message`.
12. Message Catch Event: ожидание результата асинхронной обработки.
13. Service Task external: `finalize-booking`.
14. End Event: бронирование завершено.

Альтернативные ветки:

- жильё не найдено;
- жильё недоступно;
- пользователь отменил бронирование;
- истёк срок действия черновика;
- ошибка внешней системы;
- ошибка асинхронной обработки.

## Camunda Forms

Формы находятся здесь:

- `src/main/resources/forms/search-accommodation.form`
- `src/main/resources/forms/select-accommodation.form`
- `src/main/resources/forms/confirm-booking.form`
- `src/main/resources/forms/cancel-booking.form`

Формат форм совместим с Camunda Modeler / Camunda Forms.

## Как сохраняется безопасность

Spring Security, JAAS, XML-пользователи и JWT остаются включёнными. Новые BPM endpoints
требуют аутентификации. В BPMN для пользовательских задач используются существующие роли:

- пользовательские задачи: `ROLE_USER`;
- модерация администратором и подтверждение владельцем остаются в существующем
  REST/JMS-сценарии с правами `PERM_MODERATE_DRAFTS` и
  `PERM_CONFIRM_RESERVATIONS`;
- административные операции продолжают использовать существующие authorities.

External workers выполняются внутри Spring-приложения и при необходимости временно
устанавливают системный security context для вызова защищённых сервисных методов.

## Как сохраняются транзакции

Транзакционные границы остаются в Spring-сервисах и JMS-listener'ах. Camunda Engine не
становится владельцем распределённых транзакций. External task worker вызывает уже
существующие transactional methods, например:

`worker -> ReservationDraftService.createDraft(...)`

Создание итоговой брони и сохранение PDF в EIS остаются в текущем listener-сценарии.

## Как сохраняется асинхронная обработка

ActiveMQ/Artemis, JMS-listener'ы и `JmsTaskProducer` не удаляются. Camunda запускает
асинхронный путь через существующий producer. Listener затем обновляет БД/EIS так же,
как в ЛР3. Корреляцию результата в Camunda можно расширить через message correlation;
в минимальной интеграции результат проверяется по состоянию БД и endpoint'ам договора.

## Как сохраняется Quartz

`DeleteExpiredDraftsJob` остаётся Spring Quartz job. В BPMN добавлен timer boundary для
истечения черновика и external task topic `cancel-expired-draft`, но фактическая
периодическая очистка остаётся в Quartz, чтобы не ломать ЛР3 и не переносить scheduler в
Camunda.

## Как сохраняется JCA/EIS

Существующий Jackrabbit/JCR repository остаётся. В Camunda добавлен external task
`call-eis-adapter`, который обращается к существующему EIS-адаптеру. Реальная запись
PDF-договора по-прежнему выполняется в `ReservationConfirmationJmsListener`.

## Какие файлы планируется изменить или добавить

Добавляются:

- `src/main/java/com/blps_lab1/demo/bpm/**`
- `src/main/java/com/blps_lab1/demo/controller/BpmReservationController.java`
- `src/main/resources/bpmn/airbnb-fast-booking-lab4.bpmn`
- `src/main/resources/forms/*.form`
- `src/main/resources/application.properties`
- `src/main/resources/application-local.properties`
- `src/main/resources/application-helios.properties`
- `src/main/resources/application-camunda.properties`
- `docs/lab4/**`
- `scripts/lab4/**`
- `src/test/resources/insomnia/lab4-camunda-insomnia.json`

Изменяются:

- `DemoApplication` для включения scheduled polling external tasks;
- `build.gradle` для подготовки WAR-сборки под WildFly;
- `SecurityConfig` менять не требуется, потому что BPM endpoints защищены method-level
  security.

Старые REST endpoints, сервисы, JMS-listener'ы, Quartz jobs, JCA config, security-файлы и
старые тестовые коллекции не удаляются.
