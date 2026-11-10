# ExploreWithMe — этап 2: основной сервис

Проект основан на шаблоне Яндекс Практикума и сохраняет реализацию сервиса статистики первого этапа.

## Модули

- `ewm-stats-dto` — DTO статистики.
- `ewm-stats-client` — HTTP-клиент Stats Service.
- `ewm-stats-server` — сервис статистики и отдельная PostgreSQL.
- `ewm-main-service` — основной REST-сервис ExploreWithMe и отдельная PostgreSQL.

Технологии: Java 21, Spring Boot 3.3.2, Spring Web, Spring Data JPA, Jakarta Validation, PostgreSQL, H2 для тестов, Docker Compose. Оба сервиса подключают Spring Boot Actuator.

## Реализованные API-группы

### Публичные
- `GET /events`, `GET /events/{id}` — поиск, фильтрация, сортировка и просмотр опубликованных событий.
- `GET /categories`, `GET /categories/{catId}`.
- `GET /compilations`, `GET /compilations/{compId}`.
- Каждый запрос к публичному списку событий и подробной информации о событии отправляется в Stats Service.

### Пользовательские
- Создание, просмотр и изменение собственных событий.
- Создание и просмотр заявок на участие.
- Просмотр заявок на событие и изменение их статусов инициатором.
- Отмена собственной заявки.

### Административные
- Управление категориями, подборками и пользователями.
- Поиск событий и публикация/отклонение событий.

## Запуск в Docker

Из корня репозитория:

```bash
docker compose up --build
```

- Main Service: `http://localhost:8080`
- Stats Service: `http://localhost:9090`
- Main PostgreSQL: `localhost:5432`
- Stats PostgreSQL: `localhost:5433`

У каждого сервиса собственный контейнер PostgreSQL и собственный volume. Проверка состояния: `/actuator/health`.

Остановка:

```bash
docker compose down
```

## Локальная сборка и тесты

```bash
mvn clean test
mvn clean verify
```

Для сборки основного сервиса с необходимыми модулями:

```bash
mvn -pl ewm-main-service -am clean package
```

## Ветка второго этапа

Перед работой убедитесь, что изменения первого этапа уже слиты в `main`. Затем создайте ветку второго этапа от актуального `main`:

```bash
git checkout main
git pull origin main
git checkout -b main_svc
git add .
git commit -m "Implement main service"
git push -u origin main_svc
```

Создайте Pull Request из `main_svc` в `main`. После утверждения ревью выполните Merge Pull Request на GitHub.

## Дополнительная функциональность

Для отдельной последующей ветки выбрана расширенная модерация: комментарий администратора при отклонении и повторная отправка исправленного события инициатором. По базовому плану диплома она добавляется после прохождения ревью основной части.
