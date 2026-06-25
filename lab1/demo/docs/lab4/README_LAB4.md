# Лабораторная 4: Camunda standalone BPMS

## Кратко

Пользовательский сценарий защиты больше не проходит через Insomnia. Пользователь запускает и выполняет процесс в Camunda Tasklist, а Camunda Cockpit используется для показа BPMN, токена процесса, incidents и history.

Spring Boot/WildFly backend остаётся набором адаптеров и доменных сервисов:

- JPA/Narayana transactions;
- Spring Security/JWT/JAAS;
- reservation draft и reservation services;
- Artemis/JMS;
- Jackrabbit/JCR PDF-договор;
- Quartz cleanup.

Camunda Engine запущен отдельно как standalone-сервис.

## BPMN

- файл: `src/main/resources/bpmn/airbnb-fast-booking-lab4.bpmn`
- process key: `airbnb-fast-booking`
- process name: `Airbnb Fast Booking Lab 4`
- `camunda:isStartableInTasklist="true"`
- `camunda:historyTimeToLive="180"`

## Forms

- `start-booking.form`
- `search-accommodation.form`
- `select-accommodation.form`
- `confirm-booking.form`
- `admin-moderate-reservation.form`
- `owner-approve-booking.form`
- `payment.form`
- `cancel-booking.form`

Формы не содержат demo default values. Значения вводятся вручную на защите.

## Роли в Tasklist

- USER tasks: `camunda:candidateGroups="ROLE_USER"`
- OWNER tasks: `camunda:candidateGroups="ROLE_OWNER"`
- ADMIN tasks: `camunda:candidateGroups="ROLE_ADMIN"`

В Camunda standalone нужно создать пользователей и добавить их в эти группы либо использовать demo-пользователя, которому добавлены все нужные группы.

## Подготовка demo data

Профили и жильё можно подготовить старым REST/seed способом. Это не основной BPM-сценарий, а подготовка данных.

Нужны:

- USER с фото, иначе `PaymentService` запретит оплату;
- OWNER с `requirenmentsMessage=true`, чтобы включить admin moderation и owner approval;
- ADMIN с `PERM_MODERATE_DRAFTS`;
- Place, созданный владельцем.

ID можно получить из БД:

```sql
select id, login, name, photo from users order by id;
select id, login, name from owners order by id;
select id, town, name, id_owner from places order by id;
```

## Сценарий защиты через Camunda Tasklist и Cockpit

1. Запустить Camunda standalone.
2. Задеплоить BPMN и forms:

```bash
CAMUNDA_URL=http://127.0.0.1:8080/engine-rest scripts/lab4/deploy-bpmn.sh
```

3. Запустить Spring Boot backend или WAR на WildFly/helios.
4. Открыть Camunda Tasklist.
5. Нажать `Start process`.
6. Выбрать `Airbnb Fast Booking Lab 4`.
7. Заполнить Start Form:
   - `town`
   - `arrival` в формате `YYYY-MM-DD`
   - `departure` в формате `YYYY-MM-DD`
   - `guestsAmount`
   - `petsAmount`
   - `userId`
   - `coverLetter`
8. Нажать `Start`.
9. Открыть задачу `[USER] Enter search criteria`.
10. Нажать `Claim`.
11. Проверить или поправить поля поиска.
12. Нажать `Complete`.
13. Перейти в Cockpit.
14. Открыть `Processes -> Airbnb Fast Booking Lab 4`.
15. Открыть именно running process instance, а не только process definition.
16. Показать, где стоит токен на BPMN.
17. Вернуться в Tasklist.
18. Выполнить `[USER] Select accommodation`, указать `idPlace`.
19. Выполнить `[USER] Confirm booking request`, поставить `bookingConfirmed=true`.
20. Дождаться service task `create-reservation-draft`.
21. Выполнить `[ADMIN] Moderate reservation message`, поставить `adminApproved=true`.
22. Дождаться `moderate-reservation`. Он вызывает существующую модерацию и отправляет JMS `mail.sending`.
23. Выполнить `[OWNER] Approve booking`, поставить `ownerApproved=true`.
24. Дождаться `owner-confirm-reservation`. Draft перейдёт в `[APPROVED_BY_OWNER]`.
25. Выполнить `[USER] Pay reservation`:
   - `paymentConfirmed=true`
   - `paymentType=NOW` или `LATER`
   - `paymentMethod=CARD` или `CRYPTO`
26. Показать, что дальше процесс уходит в service tasks и JMS.
27. Показать ожидание message `reservation-async-processed`.
28. После JMS listener процесс сам продолжится.
29. В Cockpit показать `Booking completed`.
30. В History показать completed process instance и заполненный `endTime`.
31. В Incidents показать отсутствие ошибок.
32. Показать PDF:

```text
/reservation/contracts/{reservationId}/pdf
```

`reservationId` также сохраняется в process variable после JMS.

## Что говорить преподавателю

Camunda управляет динамическим бизнес-процессом. Human tasks выполняются через Tasklist и Camunda Forms. Service tasks не показываются в Tasklist, потому что их выполняет Spring external task worker.

Spring Boot не заменён Camunda. Он остаётся доменным backend: транзакции, БД, security, JMS, Quartz и EIS выполняются в Spring. Camunda задаёт маршрут и состояние процесса.

JMS не переносится внутрь Camunda. Интеграция сделана через adapter/API: Spring worker отправляет сообщение в Artemis, listener выполняет обработку и коррелирует Camunda message `reservation-async-processed`.

## Сборка

```bash
./gradlew.bat clean build
```

WAR собирается для WildFly/helios.
