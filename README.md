# ExploreWithMe — этап 1: сервис статистики

Проект создан на основе официального шаблона Яндекс Практикума.

## Структура Maven
- `ewm-stats-dto` — общие DTO `EndpointHit` и `ViewStats`.
- `ewm-stats-client` — HTTP-клиент сервиса статистики.
- `ewm-stats-server` — REST API и хранение статистики.
- `ewm-main-service` — модуль основного сервиса; на этапе 1 его POM содержит только родительский модуль и `artifactId`.

## Используемые технологии
Java 21, Spring Boot 3.3.2, Spring Web, Spring Data JPA, PostgreSQL, Maven, Docker Compose.
В сервере статистики присутствует обязательный `spring-boot-starter-actuator`.

## Запуск
Из корня репозитория:
```bash
docker compose up --build
```
Stats API будет доступен на `http://localhost:9090`.
PostgreSQL доступен с хоста на порту `5433`.

В `docker-compose.yml` также описаны `ewm-service` и `ewm-db` в профиле `stage2`. На первом этапе они не запускаются, поскольку основной сервис ещё не реализуется. Не запускайте профиль `stage2`, пока основной сервис не будет реализован.

Остановка:
```bash
docker compose down
```

## API согласно `ewm-stats-service-spec.json`

### POST `/hit`
Пример тела:
```json
{
  "app": "ewm-main-service",
  "uri": "/events/1",
  "ip": "192.163.0.1",
  "timestamp": "2022-09-06 11:00:23"
}
```
Ожидаемый ответ: `201 Created` без тела.

### GET `/stats`
Обязательные параметры `start` и `end` в формате `yyyy-MM-dd HH:mm:ss`.
Необязательные параметры:
- `uris` — повторяемый query-параметр, например `uris=/events&uris=/events/1`;
- `unique` — `true` для подсчёта уникальных IP, по умолчанию `false`.

Пример:
```text
GET /stats?start=2022-09-06%2000:00:00&end=2022-09-07%2000:00:00&unique=true
```

## Проверки
Запуск тестов:
```bash
mvn -pl ewm-stats-server -am test
```
Сборка всех модулей:
```bash
mvn clean verify
```

Тесты сервера используют H2 и проверяют сохранение обращения, общую статистику, фильтрацию URI, уникальные IP и некорректный формат даты. Тесты HTTP-клиента проверяют отправку POST и получение статистики.

## Ветка этапа
Первый этап должен находиться в отдельной ветке `stat_svc`.
После создания публичного репозитория на GitHub на основе шаблона:
```bash
git checkout -b stat_svc
git add .
git commit -m "Implement statistics service"
git push -u origin stat_svc
```

## Дополнительная функциональность
Для дальнейшего этапа выбрана расширенная модерация событий: комментарии администратора при отклонении и повторная отправка исправленного события инициатором.
