
# Домашняя работа к Седьмому спринту курса Java Middle Developer (Yandex Practicum)

## Общая информация
Проект представляет собой **многомодульный Gradle‑проект SprintSevenMultiModuleGradle**, состоящий из двух сервисов:

- **ShopCatalogue** — клиентское веб‑приложение магазина
- **ShopPayment** — отдельный HTTP‑платёжный сервис

Архитектура построена на реактивном стеке **Spring WebFlux**, взаимодействие между сервисами осуществляется по HTTP.

---

## ShopCatalogue

### Контроллеры

#### CatalogueController (`/items`)
- `GET /items` — витрина товаров  
  Параметры:
    - `search` — поисковая строка
    - `sort` — сортировка (`ALPHA | PRICE | NO`)
    - `pageNumber`, `pageSize` — пагинация

- `GET /items/{id}` — карточка товара

- `POST /items` — изменение количества товара в корзине  
  Параметры:
    - `id`
    - `search`
    - `sort`
    - `pageNumber`, `pageSize`
    - `action` (`PLUS | MINUS | DELETE | NOTHING`)

- `POST /items/{id}` — изменение количества товара из карточки

#### CartController (`/cart`)
- `GET /cart/items` — содержимое корзины
- `POST /cart/items` — изменение количества товара
- `GET /cart/items/{id}` — карточка товара в корзине
- `POST /cart/items/{id}` — управление количеством

#### OrderController (`/orders`)
- `GET /orders` — список заказов
- `GET /orders/{id}` — карточка заказа  
  Параметры:
    - `id`
    - `newOrder` — флаг нового заказа

#### UserController (`/`)
- `GET /` — redirect на `/items`
- `POST /buy` — совершение покупки

---

## ShopPayment

Сервис оплаты (по умолчанию порт **8081**).

### Эндпоинты
- `GET /payment/{userId}/balance` — баланс пользователя
- `POST /payment/{userId}/buy` — оплата заказа
- `GET /swagger-ui/` — Swagger UI

---

## Сборка проекта

```bash
gradle clean build
```

Команда:
- компилирует проект
- запускает тесты
- генерирует OpenAPI‑файлы
- собирает JAR‑файлы модулей

---

## Тестирование

### Быстрый запуск
```bash
gradle test
```

### Интеграционные тесты
```bash
gradle integrationTest
```

`PaymentClientIntegrationTest` выполняется только при включённом сервисе ShopPayment.

---

## Запуск приложения

### Вариант 1 — Docker Compose
```bash
docker compose build
docker compose up
```

Приложение будет доступно по адресу:  
`http://localhost/`

### Вариант 2 — ручной запуск

PostgreSQL:
```bash
docker run --name yp-database --rm --env-file postgres.env -p 5432:5432 -v modile_two_postgres_data:/var/lib/postgresql/18/docker2 postgres:18.1
```

Redis:
```bash
docker run --name redis-server -it --rm -p 6379:6379 redis/redis-stack-server:latest
```

Запуск сервисов:
```bash
gradle clean build bootRun
```

Сначала **ShopPayment**, затем **ShopCatalogue**.

---

## Требования
- **JDK 21**
- Docker / Docker Compose

---

## Реализация и опыт

### Используемые технологии
- Spring Boot 4.0
- Spring WebFlux
- Redis (кэширование)
- PostgreSQL
- OpenAPI
- Testcontainers

### Особенности
- Версионирование кэша пользователя
- Реактивные цепочки без блокировок
- Собственная логика блокировки кнопки «Купить»
- HTTP‑клиент, сгенерированный OpenAPI

---

## Игровой режим платежей (UPDATED)

Включается в `application.yaml`.

### Поведение
- Случайный `USER_NOT_FOUND`
- Три равновероятных исхода оплаты:
    - ACCEPTED
    - REJECTED
    - UNAVAILABLE

Неудачные платежи логируются.

---

## Тесты

### ItemControllerIntegrationTest
- Поиск товаров
- Получение товара по ID
- Проверка параметров сортировки и действий

### UserServiceLimitedIntegrationTest
- Закрытие корзины
- Создание заказа
- Проверка кэша

### PaymentClientIntegrationTest
- Успешная оплата
- Пользователь не найден
- Сервис недоступен

---

## Итоги

Работа получилась насыщенной:
- Redis и OpenAPI добавили сложности
- Spring Boot 4.0 местами оказался «сыроват»
- Реактивная модель дала хороший опыт

