# Camunda Debug Guide

Все команды ниже нужны только для диагностики. Это не основной сценарий защиты.

## Process definitions

```bash
curl "http://127.0.0.1:8080/engine-rest/process-definition?key=airbnb-fast-booking&sortBy=version&sortOrder=desc"
```

## Задачи только нашего процесса

```bash
curl "http://127.0.0.1:8080/engine-rest/task?processDefinitionKey=airbnb-fast-booking&sortBy=created&sortOrder=desc"
```

## Running instances

```bash
curl "http://127.0.0.1:8080/engine-rest/process-instance?processDefinitionKey=airbnb-fast-booking"
```

## External tasks

```bash
curl "http://127.0.0.1:8080/engine-rest/external-task?processDefinitionKey=airbnb-fast-booking"
```

## Incidents

```bash
curl "http://127.0.0.1:8080/engine-rest/incident?processDefinitionKey=airbnb-fast-booking"
```

## Проверить последнюю версию process definition

```bash
curl "http://127.0.0.1:8080/engine-rest/process-definition/key/airbnb-fast-booking"
```

## Удалить старый process instance

```bash
curl -X DELETE "http://127.0.0.1:8080/engine-rest/process-instance/<PROCESS_INSTANCE_ID>?skipCustomListeners=true&skipIoMappings=true"
```

## Удалить старый deployment

```bash
curl -X DELETE "http://127.0.0.1:8080/engine-rest/deployment/<DEPLOYMENT_ID>?cascade=true"
```

## Проверить, что worker забирает tasks

1. В Cockpit открыть process instance.
2. Если стоит на service task, посмотреть external task.
3. Проверить логи backend: должен быть polling `fetchAndLock`.
4. Если появился incident, открыть incident details и посмотреть `errorMessage`.
