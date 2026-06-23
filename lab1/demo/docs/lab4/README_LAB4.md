# ЛР4: интеграция Camunda standalone BPMS

## Что изменилось по сравнению с ЛР3

В ЛР4 добавлена Camunda Platform 7 / Camunda Run как отдельный standalone BPMS-сервис.
Camunda Engine не встраивается внутрь Spring Boot приложения. Spring-приложение
обращается к Camunda через REST API и выполняет external tasks из BPMN-модели.

Старые компоненты ЛР1-ЛР3 сохранены: бизнес-сервисы, REST API, Spring Security,
PostgreSQL, MinIO, Artemis/JMS, Quartz, Narayana/JTA и интеграция с Jackrabbit EIS.

## Почему бизнес-логика теперь считается динамической

До ЛР4 порядок действий был в основном зашит в контроллерах и сервисах. Теперь порядок
сценария описан в BPMN:

`src/main/resources/bpmn/airbnb-fast-booking-lab4.bpmn`

Spring-сервисы продолжают выполнять доменную работу, но Camunda определяет, какой шаг
процесса активен: поиск, выбор жилья, проверка доступности, создание черновика, проверка
EIS, отправка асинхронного сообщения, ожидание результата, финализация или отмена.

## Где находится BPMN-модель

BPMN-файл:

`src/main/resources/bpmn/airbnb-fast-booking-lab4.bpmn`

Ключ процесса:

`airbnb-fast-booking`

Файл можно открыть в Camunda Modeler.

## Где находятся Camunda Forms

Формы пользовательских задач находятся в:

- `src/main/resources/forms/search-accommodation.form`
- `src/main/resources/forms/select-accommodation.form`
- `src/main/resources/forms/confirm-booking.form`
- `src/main/resources/forms/cancel-booking.form`

В BPMN они подключаются через `camunda:formRef`.

## Как Spring-приложение связано с Camunda standalone

Подключение к standalone Camunda REST API настраивается так:

```properties
camunda.base-url=http://127.0.0.1:8080/engine-rest
camunda.process-definition-key=airbnb-fast-booking
camunda.worker-id=airbnb-lab4-worker
camunda.external-task.lock-duration=30000
```

Новый слой Spring:

- `bpm/CamundaRestClient`
- `bpm/CamundaProcessService`
- `bpm/CamundaTaskService`
- `bpm/CamundaExternalTaskWorkers`
- `controller/BpmReservationController`

Новые endpoints:

- `POST /api/bpm/reservations/start`
- `GET /api/bpm/reservations/{processInstanceId}`
- `GET /api/bpm/tasks/my`
- `POST /api/bpm/tasks/{taskId}/complete`
- `POST /api/bpm/reservations/{processInstanceId}/cancel`

## Как сохранились роли

Spring Security, JAAS, XML-пользователи и JWT остаются включёнными. BPM endpoints
защищены через method security. В BPMN пользовательские задачи используют существующую
группу:

- `ROLE_USER` для поиска, выбора жилья и подтверждения бронирования.

Старые сервисы продолжают использовать существующие authorities:

- `PERM_PROCESS_PAYMENT`
- `PERM_MANAGE_OWN_PLACES`
- `PERM_MANAGE_USERS`
- `PERM_MODERATE_DRAFTS`
- `PERM_CONFIRM_RESERVATIONS`

## Как сохранились транзакции

Настройки Narayana/JTA остаются в `application.properties`. Транзакционные границы
по-прежнему находятся в Spring-сервисах и JMS-listener'ах. Camunda не выполняет
распределённые транзакции. External workers вызывают сервисы, где уже есть
`@Transactional`.

## Как сохранилась асинхронная обработка

Artemis/JMS остаётся механизмом асинхронной обработки. BPMN task
`send-reservation-message` вызывает `JmsTaskProducer`, который отправляет сообщение в
существующую очередь `reservation.confirmation`. Старые listener'ы обрабатывают сообщение
и обновляют PostgreSQL/EIS как раньше.

## Как сохранился Quartz

Quartz по-прежнему отвечает за периодическую очистку через `DeleteExpiredDraftsJob`.
В BPMN также есть timer для истечения черновика и external task `cancel-expired-draft`,
но это точка интеграции с BPMS, а не замена Quartz.

## Как сохранилась JCA/EIS-интеграция

Jackrabbit/JCR интеграция остаётся в `JackrabbitJcaConfig` и
`ReservationConfirmationJmsListener`. В Camunda-процесс добавлен topic
`call-eis-adapter`; worker проверяет существующий EIS adapter.

## Почему распределённую обработку и распределённые транзакции не переносили в Camunda

По заданию ЛР4 переносить распределённую обработку и распределённые транзакции внутрь
BPM-движка не требуется. Поэтому Camunda координирует бизнес-процесс, а Spring, JMS и
Narayana продолжают выполнять распределённую работу.

## Как запустить локально

Нужно поднять PostgreSQL, MinIO, Artemis и Camunda Run.

Деплой BPMN:

```bash
CAMUNDA_URL=http://127.0.0.1:8080/engine-rest scripts/lab4/deploy-bpmn.sh
```

Запуск приложения:

```bash
./gradlew bootRun --args='--spring.profiles.active=local,camunda'
```

## Как развернуть на helios/WildFly

Подробная инструкция находится здесь:

`docs/lab4/DEPLOY_WILDFLY_HELIOS.md`

## Как показать работоспособность преподавателю

1. Открыть BPMN-файл в Camunda Modeler.
2. Запустить Camunda standalone.
3. Задеплоить BPMN.
4. Запустить Spring-приложение.
5. Авторизоваться через `/auth/login`.
6. Запустить процесс через `/api/bpm/reservations/start`.
7. Открыть Camunda Tasklist и выполнить user tasks.
8. Показать, что external tasks выполняются Spring worker'ами.
9. Показать логи JMS listener'ов и результат бронирования/договора.
10. Показать, что Quartz job остался в конфигурации.
