# Заметки для защиты Lab4

## Главная формулировка

Пользовательский интерфейс реализован через Camunda Tasklist и Camunda Forms. Пользователь не вызывает REST API приложения вручную для прохождения процесса. Он запускает process instance и выполняет User Task через формы.

Camunda Engine управляет маршрутом BPMN-процесса. Spring backend используется как набор external task adapters и доменных сервисов: БД, транзакции, JMS, Quartz, EIS и PDF остаются в Spring.

## Роль Tasklist

Tasklist нужен для human tasks:

- USER вводит параметры и оплачивает;
- ADMIN модерирует сообщение;
- OWNER подтверждает или отклоняет бронирование.

Tasklist показывает задачи по candidate groups. Если пользователь не видит задачу, проверить группы `ROLE_USER`, `ROLE_OWNER`, `ROLE_ADMIN`.

## Роль Cockpit

Cockpit нужен для:

- просмотра BPMN;
- просмотра running process instance;
- проверки, где стоит токен;
- просмотра incidents;
- просмотра history;
- проверки process variables.

Service Task не появляются в Tasklist, потому что их забирает Spring worker через Camunda REST `fetchAndLock`.

## Claim, Save, Complete

- `Claim` назначает задачу текущему пользователю.
- `Save` сохраняет форму, но не двигает процесс.
- `Complete` завершает human task и передаёт процесс дальше.
- После `Complete` задача исчезает из Tasklist, потому что токен ушёл на следующий BPMN element.

## Реальный процесс

1. USER выбирает жильё и создаёт draft.
2. Draft получает `[PENDING_ADMIN]`.
3. ADMIN одобряет или отклоняет.
4. При approval draft получает `[APPROVED_BY_ADMIN]`, отправляется JMS `mail.sending`.
5. Mail listener переводит draft в `[EMAIL_SENT_TO_OWNER]`.
6. OWNER одобряет или отклоняет.
7. При approval draft получает `[APPROVED_BY_OWNER]`.
8. USER оплачивает.
9. Spring worker валидирует оплату и отправляет JMS `reservation.confirmation`.
10. JMS listener создаёт `Reservation`, удаляет draft, генерирует PDF в Jackrabbit.
11. Listener коррелирует `reservation-async-processed`.
12. Camunda завершает процесс.

## Почему Insomnia больше не основной сценарий

Insomnia из `tests7.json` использовалась как источник понимания старого workflow и может остаться diagnostic/dev инструментом. На защите процесс выполняется через Camunda UI.

## Что показать

- BPMN `Airbnb Fast Booking Lab 4`.
- Start form в Tasklist.
- USER/ADMIN/OWNER tasks с разными candidate groups.
- External tasks в Cockpit.
- JMS wait event `reservation-async-processed`.
- Завершённый process instance.
- Отсутствие incidents.
- PDF endpoint `/reservation/contracts/{reservationId}/pdf`.
