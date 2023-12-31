# Explore With Me
Используемые технологии и инструменты:
-  Java 11, Spring Boot, Spring Data JPA, Hibernate, Docker. 
## Идея

Данное приложение это — афиша. В этой афише можно предложить какое-либо событие от выставки до похода в кино и собрать компанию для участия в нём.
![image](https://github.com/PiFFFko/java-explore-with-me/assets/38191066/6fd67c32-e195-470c-aea0-8a9fe4226fae)

Приложение состоит из двух сервисов: 
- **основной сервис** будет содержать всё необходимое для работы продукта;
- **сервис статистики** будет хранить количество просмотров и позволит делать различные выборки для анализа работы приложения.

## Основной сервис

API основного сервиса разделен на три части:
- публичная доступна без регистрации любому пользователю сети и должен предоставлять возможности поиска и фильтрации событий;
- закрытая доступна только авторизованным пользователям и предназначена для создания и редактирования событий и для подачи заявок на участие в событиях;
- административная часть для администраторов сервиса. Предназначена для добавления, изменения и удаления категорий для событий, управления пользователями, модераций событий, комментариев.

## Сервис статистики
Сервис статистики собирает информацию о посещении приложения. 
Во-первых, о количестве обращений пользователей к спискам событий и, во-вторых, о количестве запросов к подробной информации о событии. На основе этой информации должна формироваться статистика о работе приложения.

## Спецификация API
Для обоих сервисов имеютяс подробные спецификации API:
- спецификация основного сервиса: [API основного сервиса](https://raw.githubusercontent.com/yandex-praktikum/java-explore-with-me/main/ewm-main-service-spec.json)
- спецификация сервиса статистики: [API сервиса статистика](https://raw.githubusercontent.com/yandex-praktikum/java-explore-with-me/main/ewm-stats-service-spec.json)
