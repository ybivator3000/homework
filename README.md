# Homework

Учебный командный Java-проект.

## Технологии

- Java 21
- Maven
- Hibernate ORM 7.4.13. Final
- PostgreSQL (Neon)

## Подключение к базе данных

Проект использует PostgreSQL, размещённую в [Neon](https://console.neon.tech/), и Hibernate ORM для взаимодействия с базой данных.

### Доступ к Neon

Для работы с общей базой данных необходимо получить доступ к проекту `user-service` в организации `user-service-team`.

База данных: `user_service_db`.

Доступ к проекту предоставляется администратором команды через приглашение в Neon.

### Настройка подключения

Параметры подключения передаются через переменные окружения:

| Переменная    | Назначение                        |
|---------------|-----------------------------------|
| `DB_URL`      | JDBC URL подключения к PostgreSQL |
| `DB_USER`     | Имя пользователя PostgreSQL       |
| `DB_PASSWORD` | Пароль пользователя PostgreSQL    |

Для настройки в IntelliJ IDEA:

1. Открыть **Run → Edit Configurations**.
2. Создать конфигурацию **Application** для класса `ru.aston.Main` или выбрать существующую.
3. В поле **Environment variables** указать `DB_URL`, `DB_USER`, `DB_PASSWORD`.
4. Сохранить конфигурацию.

Пример JDBC URL:

`jdbc:postgresql://<host>/user_service_db?sslmode=require`

Актуальные параметры подключения можно получить в Neon Console через **Connect** после предоставления доступа.

**Важно:** не добавлять пароли и другие секретные данные в Git.

### Конфигурация Hibernate

Файл `src/main/resources/hibernate.cfg.xml` содержит настройки Hibernate.

Класс `ru.aston.config.HibernateUtil` отвечает за создание `SessionFactory` и получение параметров подключения из переменных окружения.

Используется `hibernate.hbm2ddl.auto=update` для автоматического обновления схемы базы данных на основе зарегистрированных сущностей. Изменения структуры общей базы необходимо согласовывать с командой.