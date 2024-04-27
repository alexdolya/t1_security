# Overview

В приложении реализован функционал аутентификации и авторизации с использованием Spring Security и JWT

Реализовано 2 роли пользователя: USER, ADMIN
При регистрации пользователя необходимо выбрать одну из 2-х ролей.

 ##### Взаимодействие с приложением:
- регистрация пользователя
- авторизация пользователя
- обновления access токена с помощью refresh токена
- ручка с доступом ТОЛЬКО для пользователей с ролью USER
- ручка с доступом ТОЛЬКО для пользователей с ролью ADMIN
- ручка с доступом для всех аутентифицированных пользователей

#### Open-API: src/main/resources/openapi/open-api.json

## Запуск приложения

Запуск приложения и БД происходит в контейнере с помощью docker-compose. Выполните следующие инструкции.

1. Клонируйте репозиторий

2. В терминале перейдите в папку с проектом.

3. В терминале выполните следющую команду
    ```
    docker-compose up
    ```

4. После сборки приложение по умолчанию запустится на порте 8080, Swagger UI представлен по адресу /swagger-ui/index.html

## Технологии

При разработке использован следующий стэк технологий:
- Java 17
- Spring Boot 3.2.5
- Spring Securoty
- Spring Data JPA
- PostgreSQL
- Lombok
- OpenAPI
