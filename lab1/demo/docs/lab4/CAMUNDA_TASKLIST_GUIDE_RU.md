# Camunda Tasklist и Cockpit: инструкция для защиты

## Что открыть

- Tasklist: `http://<camunda-host>:8080/camunda/app/tasklist`
- Cockpit: `http://<camunda-host>:8080/camunda/app/cockpit`

## Как не перепутать задачи

В стандартной Camunda могут быть demo-задачи:

- `Review Invoice`
- `Assign Reviewer`
- `Prepare Bank Transfer`
- `Approve Invoice`

Их игнорировать. Нужны только задачи процесса `Airbnb Fast Booking Lab 4`.

В Tasklist используйте фильтр по process definition/name или смотрите название task:

- `[USER] Enter search criteria`
- `[USER] Select accommodation`
- `[USER] Confirm booking request`
- `[ADMIN] Moderate reservation message`
- `[OWNER] Approve booking`
- `[USER] Pay reservation`

## Как выполнить задачу

1. Открыть задачу.
2. Нажать `Claim`.
3. Заполнить форму.
4. Нажать `Complete`.

`Save` только сохраняет значения формы. `Complete` двигает процесс дальше.

## Как смотреть процесс в Cockpit

1. Открыть Cockpit.
2. Перейти в `Processes`.
3. Выбрать `Airbnb Fast Booking Lab 4`.
4. Проверить версию process definition. После нового деплоя версия увеличивается.
5. Открыть именно running process instance.
6. Синий маркер показывает, где сейчас стоит токен.
7. Красный маркер означает incident.
8. Если process instance старый, он может быть на версии 1/2, а новый deployment уже версия 3/4. Для защиты стартовать новый instance после последнего deploy.

## Почему процесс может стоять на service task

Service task выполняется не человеком, а Spring worker. Если процесс стоит на service task:

- проверить, запущен ли backend;
- проверить `camunda.base-url`;
- проверить external tasks;
- проверить incidents.

## Почему процесс стоит на message event

На `reservation-async-processed` Camunda ждёт JMS result. Spring JMS listener должен после обработки сам вызвать Camunda REST `/message`.

Ручной запрос из Insomnia не нужен.
