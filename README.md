# SearchEngine -локальный поисковый движок

## Описание
Проект создания локальной поисковой системы, дающей возможность полнотекстового поиска по сайтам, указанным в конфигурационном файле. Система содержит несколько контроллеров, сервисов и репозиторий подключенный к БД MySQL.
На стартой странице системы выводится статистическая информация ("DASHBOARD"), о проиндексированных сайтах и страницах, а также леммах (начальная словарная форма слова), содержащихся на этих страницах

![проект](https://github.com/Misha7547/searchengines/assets/117103365/0e7ae8c2-997f-4121-9494-c0a020075805)

Система позволяет производить как полную индексацию всех страниц на сайтов из списка, так и добавление и переиндексацию отдельно заданных страниц этих сайтов.

![проект 2](https://github.com/Misha7547/searchengines/assets/117103365/12d4cbf1-f94d-4fa8-9e8c-fcfab001fb62)

В строку запроса для поиска можно вводить как одно слово, так и целую фразу. При этом можно выбирать, где искать - на конкретном сайте или выбрать все сайты.

![проект 3 ](https://github.com/Misha7547/searchengines/assets/117103365/af7f1c37-1795-472d-bc72-408969c8c16e)
В результате поиска выводится список наиболее релевантных страниц, где встречаются слова из строки запроса.


## Стек используемых технологий

Java Core, Spring Boot, JPA, Hibernate, JDBC, Security, MySQL, REST API, JSOUP, Maven, Git, Swagger
Также библиотеки лемматизации - RussianMorphology и стемминга (нахождения основы слова) - stemmer.

## Настройки для запуска

### Зависимости

Для успешного скачивания и подключения к проекту зависимостей из GitHub необходимо настроить Maven конфигурацию в файле settings.xml.

<parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.7.1</version>
        <relativePath/>
</parent>

а также ссылку на репозиторий для скачивания зависимостей лемматизатора:

<repositories>
        <repository>
            <id>skillbox-gitlab</id>
            <url>https://gitlab.skillbox.ru/api/v4/projects/263574/packages/maven</url>
        </repository>
</repositories>

Также нужно указать подключение следующих зависимостей apache Maven:

spring-boot-starter-web
spring-boot-starter-thymeleaf
spring-boot-starter-data-jpa
mysql-connector-java
lombok
jsoup

Для работы парсинга страниц нужно подключить JSOUP :

<dependency>
            <groupId>org.jsoup</groupId>
            <artifactId>jsoup</artifactId>
            <version>1.15.4</version>
</dependency>

Для преобразования слов в леммы неообходимо подключение зависимостей morph, morphology, dictionary-reader, english, russianиз источника : org.apache.lucene.morphology необходимо ещё создать (либо отредактировать если он имеется - в Windows он располагается в директории C:/Users/<Имя вашего пользователя>/.m2) файл settings.xml, в котором указать токен для получения данных из публичного репозитория. В файл нужно внести следующие строки:

<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
 https://maven.apache.org/xsd/settings-1.0.0.xsd">

<servers>
    <server>
      <id>skillbox-gitlab</id>
      <configuration>
        <httpHeaders>
          <property>
            <name>Private-Token</name>
            <value>wtb5axJDFX9Vm_W1Lexg</value>
          </property>
        </httpHeaders>
      </configuration>
    </server>
  </servers>
</settings>

### Запуск

Стартовая страница поискового движка находится по адресу : http://localhost:8080/
Сразу при старте система запрашивает логин/пароль, которые указаны в файле конфигурации src/resources/application.yml:

spring:
  datasource:
    username: 
    password: 
    url:

    
