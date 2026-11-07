# Kafka Integration — User Service

## 1. Общая информация

В рамках ДЗ №5 реализована интеграция `user-service` с Apache Kafka.

`user-service` выступает в роли Producer и публикует события при создании и удалении пользователя.

Отправка уведомлений по электронной почте будет реализована в отдельном проекте `notification-service`, который выступает в роли Consumer.

Оба сервиса взаимодействуют через Kafka Broker и не зависят друг от друга на уровне Java-классов.

## 2. Инфраструктура

Для локального запуска Kafka используется Docker Compose.

- Kafka version: `4.1.2`
- Docker Compose: `docker-compose.yml`
- Bootstrap server: `localhost:9092`
- Topic: `user-events`

Запуск Kafka из корневой папки проекта:

```bash
docker compose up -d
```

Проверка состояния контейнера:

```bash
docker ps
```

Остановка Kafka:

```bash
docker compose down
```

## 3. Формат Kafka-события

Для взаимодействия между сервисами используется JSON.

Пример события создания пользователя:

```json
{
  "email": "user@example.com",
  "operation": "CREATE"
}
```

Пример события удаления пользователя:

```json
{
  "email": "user@example.com",
  "operation": "DELETE"
}
```

Поля:

| Поле | Тип | Описание |
|---|---|---|
| `email` | String | Email пользователя |
| `operation` | String | Тип операции: `CREATE` или `DELETE` |

Важно: Consumer должен использовать именно этот формат сообщения.

## 4. Реализация Producer

В `user-service` добавлены следующие компоненты:

- `UserEvent` — DTO события, содержит email и тип операции.
- `UserEventProducer` — отправляет события в Kafka.
- `UserEventListener` — обрабатывает события приложения после успешного завершения транзакции.

### Публикация событий

События создаются в `UserServiceImpl`:

- После создания пользователя публикуется `CREATE`.
- При удалении пользователя публикуется `DELETE`.

Для публикации используется `ApplicationEventPublisher`.

`UserEventListener` использует:

```java
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
```

Это означает, что Kafka-событие отправляется только после успешного завершения транзакции базы данных.

При откате транзакции событие не отправляется.

### Отправка в Kafka

`UserEventProducer` отправляет сообщения в топик `user-events`.

В качестве ключа сообщения используется email пользователя.

Используется асинхронная отправка через `KafkaTemplate`.

## 5. Конфигурация

В `application.properties` добавлены настройки:

```properties
spring.kafka.bootstrap-servers=localhost:9092

spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
```

## 6. Что необходимо учесть при реализации Consumer

При разработке `notification-service` необходимо:

1. Подключиться к Kafka Broker по адресу `localhost:9092`.
2. Подписаться на топик `user-events`.
3. Настроить десериализацию JSON в соответствующий DTO.
4. Обрабатывать операции `CREATE` и `DELETE`.
5. Использовать поле `email` для определения получателя уведомления.

Тексты уведомлений согласно заданию:

**CREATE:**

> Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.

**DELETE:**

> Здравствуйте! Ваш аккаунт был удалён.

DTO можно реализовать независимо в `notification-service`, сохранив совместимость с JSON-контрактом выше.

## 7. Проверка работоспособности

Для ручной проверки сообщений можно запустить Kafka Console Consumer:

```bash
docker exec -it homework-kafka /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic user-events --from-beginning
```

После создания или удаления пользователя соответствующее событие должно появиться в консоли.

### Результаты проверки

- Создание пользователя через REST API — проверено.
- Получение события `CREATE` через Kafka Consumer — проверено.
- Удаление пользователя через REST API — проверено.
- Получение события `DELETE` через Kafka Consumer — проверено.
- Все 55 тестов `user-service` успешно проходят.

## 8. Важное замечание

Отправка Kafka-события выполняется после коммита транзакции БД. Это защищает от отправки событий при откате транзакции, но не обеспечивает атомарность между БД и Kafka.

Если Kafka недоступна после успешного коммита транзакции, сообщение может быть потеряно. Для учебной реализации используется текущий подход без Transactional Outbox.