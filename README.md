# kotlin-shop

Бэкенд интернет-магазина на Kotlin + Ktor. Курсовой проект.

Стек: Kotlin 2.0, Ktor 3.0, PostgreSQL, Redis, RabbitMQ, JWT, Docker.

## Запуск

Скопировать `.env.example` в `.env`, затем:

```bash
docker-compose up --build
```

Приложение поднимается на `http://localhost:8080`.
Swagger: `http://localhost:8080/swagger`
RabbitMQ UI: `http://localhost:15672` (guest / guest)

Если нужно запустить без Docker (только инфраструктура в контейнерах):

```bash
docker-compose up postgres redis rabbitmq -d
./gradlew :app:run
```

## API

**Аутентификация**

```
POST /auth/register   — регистрация
POST /auth/login      — получить JWT токен
```

**Товары** (публичные)

```
GET  /products        — список
GET  /products/{id}   — по ID
```

**Заказы** (нужен токен в `Authorization: Bearer ...`)

```
POST   /orders        — создать заказ
GET    /orders        — мои заказы
DELETE /orders/{id}   — отменить заказ
```

**Админ** (нужен токен с ролью ADMIN)

```
POST   /products      — добавить товар
PUT    /products/{id} — обновить
DELETE /products/{id} — удалить
GET    /stats/orders  — статистика
```

## Тесты

```bash
./gradlew test
```

Юнит-тесты отдельно:
```bash
./gradlew :services:test
```

Интеграционные (нужен Docker):
```bash
./gradlew :infrastructure:test
```

## Структура

```
domain/          — модели и интерфейсы
services/        — бизнес-логика
infrastructure/  — БД, Redis, RabbitMQ
api/             — роуты, DTO, плагины
app/             — точка входа
```
