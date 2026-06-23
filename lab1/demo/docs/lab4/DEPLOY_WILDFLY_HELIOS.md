# Развёртывание ЛР4 на WildFly / helios

## Локальная сборка

Запуск тестов:

```bash
./gradlew clean test
```

Сборка исполняемого jar и WAR:

```bash
./gradlew clean bootJar bootWar
```

WAR-файл будет создан в каталоге:

`build/libs/`

## Запуск Camunda standalone

Camunda должна запускаться отдельно от Spring-приложения. Пример для Camunda Run:

```bash
export CAMUNDA_HOME=$HOME/camunda-run
export CAMUNDA_PORT=8080
scripts/lab4/start-camunda-local.sh
```

Проверка Camunda:

```bash
CAMUNDA_URL=http://127.0.0.1:8080/engine-rest scripts/lab4/check-camunda.sh
```

Деплой BPMN:

```bash
CAMUNDA_URL=http://127.0.0.1:8080/engine-rest scripts/lab4/deploy-bpmn.sh
```

BPMN-файл:

`src/main/resources/bpmn/airbnb-fast-booking-lab4.bpmn`

## Деплой приложения в WildFly

Собрать WAR:

```bash
./gradlew clean bootWar
```

Скопировать WAR на helios:

```bash
scp -P 2222 build/libs/demo-0.0.1-SNAPSHOT.war s408560@se.ifmo.ru:/home/studs/s408560/
```

Подключиться к helios:

```bash
ssh -p 2222 s408560@se.ifmo.ru
```

Дальше WAR нужно задеплоить способом, который используется в учебной среде: через
deployment directory WildFly или через WildFly CLI.

Профили запуска для серверной среды:

```bash
--spring.profiles.active=helios,camunda
```

## Используемые порты

- `8080`: Camunda standalone REST API / Tasklist / Cockpit, если Camunda доступна
  локально на helios.
- `28082`: первый узел приложения на helios.
- `28083`: второй узел приложения на helios для проверки распределённой обработки.
- `61616`: Artemis broker.
- `9000` или `29000`: MinIO, зависит от окружения.
- PostgreSQL: локальная БД helios, база `studs`.

## Подключение к helios

Обычное подключение:

```bash
ssh -p 2222 s408560@se.ifmo.ru
```

Подключение с пробросом портов для Insomnia:

```bash
ssh -p 2222 \
  -L 8082:127.0.0.1:28082 \
  -L 8083:127.0.0.1:28083 \
  -R 61616:127.0.0.1:61616 \
  s408560@se.ifmo.ru
```

`8082` и `8083` используются локально для проверки двух узлов приложения через
Insomnia. Порт `61616` связан с Artemis MQ.

## Профили конфигурации

Основной файл:

`src/main/resources/application.properties`

Дополнительные профили:

- `application-local.properties`
- `application-helios.properties`
- `application-camunda.properties`

Старые настройки не удаляются. Настройки Camunda добавлены как новые свойства.

## Проверка процесса

1. Импортировать `src/test/resources/insomnia/lab4-camunda-insomnia.json`.
2. Авторизоваться пользователем `KaragodinaKs@yandex.ru`.
3. Сохранить полученный JWT в переменную окружения Insomnia `token`.
4. Выполнить запрос `Start BPM Reservation`.
5. Открыть Camunda Tasklist или вызвать `My BPM Tasks`.
6. Завершить пользовательские задачи.
7. Смотреть логи Spring-приложения: должны выполняться external workers и JMS.
8. Проверить, что старые endpoints работают, например `/places/rating`.
9. Проверить, что endpoint договора сохранился:
   `/reservation/contracts/{reservationId}/pdf`.

## Запуск двух узлов

Для проверки распределённой обработки можно запустить два экземпляра приложения с
разными значениями `server.port`, например `28082` и `28083`. Оба узла используют одну
PostgreSQL, один Artemis и одну standalone Camunda. External task workers могут работать
на обоих узлах; Camunda lock гарантирует, что конкретную external task выполняет только
один worker.
