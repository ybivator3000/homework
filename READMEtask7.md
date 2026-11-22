# Spring Cloud Integration: API Gateway & External Configuration

## 1. Описание

В рамках домашнего задания №7 в проект добавлены два паттерна микросервисной архитектуры:

- **API Gateway** — единая точка входа для HTTP-запросов к микросервисам.
- **External Configuration** — централизованное хранение и предоставление конфигураций микросервисам.

Реализация выполнена с использованием Spring Cloud Gateway и Spring Cloud Config.

На текущем этапе API Gateway использует статические адреса микросервисов. Переход на динамическую маршрутизацию через Netflix Eureka будет выполнен после завершения реализации Service Discovery.

## 2. Структура проекта

В проект добавлены следующие Maven-модули:

| Модуль                 | Назначение                                  |
| ---------------------- | ------------------------------------------- |
| `user-service`         | Сервис управления пользователями            |
| `notification-service` | Сервис уведомлений                          |
| `config-server`        | Централизованное управление конфигурациями  |
| `api-gateway`          | Единая точка входа и маршрутизация запросов |

## 3. External Configuration — Config Server

Создан отдельный модуль `config-server`.

**Основные характеристики:**

- Используется Spring Cloud Config Server.
- Сервер запускается на порту `8888`.
- В качестве хранилища конфигураций используется Native Profile.
- Конфигурационные файлы расположены в `config-server/src/main/resources/config-repo/`.

Для микросервисов созданы отдельные конфигурации:

- `user-service.properties`
- `notification-service.yml`
- `api-gateway.yml`

### Подключение Config Client

Для получения централизованных настроек в `user-service` и `notification-service` добавлен Spring Cloud Config Client.

В конфигурации сервисов указан адрес Config Server:

`http://localhost:8888`

Используется механизм `spring.config.import`.

Таким образом, параметры подключения и настройки сервисов хранятся централизованно, а не дублируются в конфигурациях каждого приложения.

## 4. API Gateway

Создан отдельный Maven-модуль `api-gateway`.

**Используемые технологии:**

- Spring Cloud Gateway Server WebFlux
- Spring Cloud Config Client
- Spring Boot Actuator

Gateway запускается на порту `8081` и получает настройки маршрутизации от Config Server.

### Настроенные маршруты

На текущем этапе используются статические адреса:

| Route ID               | Входящий путь              | Адрес назначения        |
| ---------------------- | -------------------------- | ----------------------- |
| `user-service`         | `/api/users/**`            | `http://localhost:8080` |
| `notification-service` | `/api/v1/notifications/**` | `http://localhost:8082` |

Gateway сохраняет исходный путь запроса при его передаче микросервису.

### Actuator

Для диагностики Gateway подключён Spring Boot Actuator.

Настроен доступ только для чтения к информации о маршрутах.

Для просмотра зарегистрированных маршрутов используется endpoint:

`http://localhost:8081/actuator/gateway/routes`

## 5. Изменения Maven-конфигурации

Для интеграции новых компонентов потребовались изменения не только в новых модулях, но и в существующих Maven-файлах проекта.

### Корневой pom.xml

В корневой POM добавлены модули:

```xml
<module>config-server</module>
<module>api-gateway</module>
```

Также в корневой конфигурации используется Spring Cloud BOM для централизованного управления версиями зависимостей Spring Cloud.

Основные версии проекта:

- Java 21
- Spring Boot 4.1.2
- Spring Cloud 2025.1.4

### user-service/pom.xml

В существующий модуль добавлена зависимость:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

Она необходима для подключения User Service к Config Server.

### notification-service/pom.xml

Аналогично добавлена зависимость Spring Cloud Config Client:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

Она обеспечивает получение централизованной конфигурации Notification Service.

### config-server/pom.xml

Создан отдельный Maven-модуль с зависимостью Spring Cloud Config Server и необходимой конфигурацией Spring Boot.

### api-gateway/pom.xml

Создан отдельный Maven-модуль со следующими зависимостями:

- `spring-cloud-starter-gateway-server-webflux`
- `spring-cloud-starter-config`
- `spring-boot-starter-actuator`

Таким образом, изменения в POM-файлах существующих микросервисов связаны с интеграцией общей инфраструктуры конфигурации, а не с изменением их основной бизнес-логики.

## 6. Текущее состояние и ограничения

На данный момент реализованы и проверены следующие возможности:

- Config Server успешно запускается на порту `8888`.
- Конфигурации микросервисов доступны через Config Server.
- User Service и Notification Service настроены как Config Clients.
- API Gateway успешно запускается на порту `8081`.
- Gateway получает конфигурацию маршрутизации от Config Server.
- Оба маршрута успешно регистрируются и отображаются через Actuator.

**Текущее ограничение — статическая маршрутизация.**

В конфигурации Gateway пока указаны адреса:

- `http://localhost:8080`
- `http://localhost:8082`

Переход на динамическую маршрутизацию через Netflix Eureka запланирован после завершения реализации Service Discovery.

Полноценное сквозное тестирование запросов через Gateway также пока не завершено. При запуске User Service возникла проблема с обрывом соединения с удалённой базой данных Neon PostgreSQL.

## 7. Дальнейшие этапы интеграции

После завершения работы остальных участников команды планируется:

1. Интегрировать Eureka Server и регистрацию микросервисов.
2. Подключить API Gateway к Eureka.
3. Перевести маршруты Gateway со статических адресов на динамическое обнаружение сервисов.
4. Интегрировать Circuit Breaker на основе Resilience4j.
5. Проверить совместную работу API Gateway, Config Server, Eureka и Circuit Breaker.
6. Провести сквозное тестирование маршрутизации и обработки ошибок.

## 8. Git

Работа выполнена в ветке:

`feature/api-gateway-config-server`

Основные коммиты:

| Commit    | Описание                                            |
| --------- | --------------------------------------------------- |
| `94235d4` | `feat: integrate services with Spring Cloud Config` |
| `daef182` | `feat: add Spring Cloud API Gateway`                |

Изменения отправлены в удалённый репозиторий GitHub.
