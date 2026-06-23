# Краткие заметки для защиты ЛР4

## Что было реализовано в ЛР1-ЛР3

В ЛР1-ЛР3 был реализован Airbnb-подобный сервис быстрого бронирования жилья на Spring
Boot. В проекте уже были REST API, PostgreSQL, BPMN-описание процесса, JTA/Narayana
транзакции, Spring Security с JAAS/XML-пользователями/JWT, асинхронная обработка через
Artemis/JMS/STOMP, Quartz jobs и интеграция с внешней информационной системой через
Jackrabbit/JCR.

## Что добавлено в ЛР4

В ЛР4 добавлена Camunda как standalone BPMS. Camunda Engine не встроен внутрь Spring Boot.
Camunda Run хранит исполняемую BPMN-модель, user tasks, Tasklist, Cockpit и формы.
Spring-приложение теперь работает как внешний клиент и worker.

## Как теперь работает процесс

Порядок шагов процесса теперь хранится в BPMN-файле:

`src/main/resources/bpmn/airbnb-fast-booking-lab4.bpmn`

Пользовательские задачи собирают критерии поиска, выбранное жильё и подтверждение
бронирования. Сервисные задачи описаны как external tasks. Их topics обрабатываются
Spring worker'ами.

## Какие задачи выполняет Camunda

Camunda:

- хранит исполняемую BPMN-модель;
- создаёт process instances;
- управляет user tasks и candidate groups;
- создаёт external tasks для Spring-приложения;
- содержит timer и message events для отмены, истечения срока и ожидания асинхронного
  результата.

## Какие задачи остались в Spring

Spring-приложение:

- аутентифицирует пользователей через JWT/JAAS;
- выполняет доменные сервисы;
- сохраняет транзакционные границы Narayana/JTA;
- отправляет и получает JMS-сообщения;
- выполняет Quartz cleanup;
- взаимодействует с Jackrabbit EIS.

## Почему Camunda standalone

Standalone-режим выбран потому, что задание ЛР4 требует запускать BPM-движок отдельным
сервисом. Поэтому в Spring Boot не добавлялся embedded Camunda Engine.

## Почему external task pattern

External task pattern позволяет оставить существующие Spring-сервисы, безопасность,
транзакции, JMS, Quartz и EIS-интеграцию внутри приложения. Camunda только управляет
последовательностью процесса, а фактическая бизнес-операция выполняется там, где она уже
была реализована.

## Как работает интеграция с JMS

Camunda создаёт external task `send-reservation-message`. Spring worker вызывает
`JmsTaskProducer`, который отправляет сообщение в Artemis. Существующий JMS listener
обрабатывает сообщение, создаёт итоговую бронь и сохраняет PDF-договор.

## Как работает интеграция с Quartz

Quartz сохранён как реальный механизм периодической очистки черновиков. В BPMN добавлен
timer и topic `cancel-expired-draft`, чтобы показать точку интеграции BPMS, но scheduler
не переносился в Camunda.

## Как сохранена безопасность

Spring Security, method-level annotations и XML-пользователи остались. BPMN user tasks
используют существующую группу `ROLE_USER`. Service tasks выполняются worker'ами внутри
Spring-приложения.

## Как сохранены транзакции

Worker'ы вызывают существующие `@Transactional` сервисы и JMS-listener'ы. Распределённые
транзакции не переносятся в Camunda, потому что по заданию это не требуется и потому что
Narayana/JTA уже настроены на стороне Spring-приложения.
