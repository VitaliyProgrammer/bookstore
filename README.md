# 📚 BookStore Application - Spring Boot Back-End

![Java](https://img.shields.io/badge/Java-19-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-32CD32)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6.2.3-FF69B4)
![JWT](https://img.shields.io/badge/JJWT-0.11.5-orange)
![MySQL](https://img.shields.io/badge/MySQL-8.3-FFD700)
![Hibernate](https://img.shields.io/badge/Hibernate-6.4.4.Final-4B0082)
![Liquibase](https://img.shields.io/badge/Liquibase-4.24.0-blue)
![Architecture](https://img.shields.io/badge/Architecture-Layered-brightgreen)
![DTO](https://img.shields.io/badge/DTO-Mapping-yellow)
![Specification](https://img.shields.io/badge/Specification-Pattern-lightgrey)
![Exception](https://img.shields.io/badge/Exception-Handling-red)
![Repository](https://img.shields.io/badge/Repository-Pattern-purple)
![Security](https://img.shields.io/badge/Security-Pattern-teal)
![Validation](https://img.shields.io/badge/Validation-Spring%20Validation-green)
![Testing](https://img.shields.io/badge/Testing-JUnit%205%20%7C%20Mockito%205.7.0%20%7C%20Integration-blueviolet)
![Swagger](https://img.shields.io/badge/Swagger%20UI-5.13.0-85EA2D)


## 📌 Introduction

**BookStore** is a fully-featured application developed as part
of my Java Back-End engineering learning journey. It replicates the core  
functionality of a modern online bookstore and demonstrates how real e-commerce systems handle:

- authorization & authentication
- catalog management
- shopping cart workflow
- and order processing


The goal of this project is not simply to build CRUD endpoints, but to design
a **clean, scalable, and secure architecture** that follows production-grade principles such as:

- layered architecture
- DTO mapping
- custom exception handling
- SpecificationProvider
- SpecificationBuilder
- service-level security
- JWT-based authentication
- service-level validation
- liquibase
- mocking and integration testing
- MySQL database


## 🎯 Motivation

This project was motivated by the desire to build something more complex than a basic REST API -
a system that reflects real business logic and can serve as a foundation for
enterprise-level services

While working on BookStore, I focused on solving several important engineering problems:

- 🔗 How to manage entity relationships professionally(OneToOne, OneToMany,
  ManyToOne, ManyToMany, to join tables)?
- 🔄 How to avoid common issues such as recursion during serialization?
- 🔐 How to implement secure JWT authentication with roles?
- 🧩 How to organize controllers, services, and repositories into clean architecture?
- 🧪 How to write mocking and integration tests that simulate real workflow
  (register -> login -> add books -> create order)?
- 📦 How to design predictable REST endpoints for real clients?
- 🛒 How to correctly implement a shopping cart(add item, update quantity,
  merge items)?

During development, I faced and solved challenges related to transaction management,
DTO design, Spring Security pitfalls, JPA lazy loading behavior, Spring Boot Testing pitfalls, and error handling.
These hurdles significantly strengthened my understanding of back-end engineering.


## 🚀 What this project demonstrates

Bookstore is not just a learning exercise - it is a demonstration of engineering mindset:


- 🧠 Clean and maintainable system design
- ✨ Readable and structure business logic
- 🌍 Real-world use cases and workflow
- 📐 Application of professional patterns used in production
- 📚 Influence from high-quality open-source projects

The project became my practical playground for experimenting with architecture,
security, database, modeling, and writing code that meets production-quality standards.


## 📌 Features / Functionality

This BookStore project demonstrates core back-end functionalities of a modern
online bookstore.
It is designed to simulate real-world e-commerce scenarios and provide
a foundation for production-level services.

 👤 **User registration and Authentication**       
 - Register and login
 - JWT tokens issuance
 - Role-based access control (USER / ADMIN) 

📚 **Book management**
- Create, read, update, and delete books
- REST-based catalog management
- Works with DTOs, validation, exception handling

🛒 **Shopping cart workflow**
- Add books to the shopping cart
- Increase / decrease quantity
- Merge identical cart-items
- View current cart-items contents

📦 **Order processing**
- Generate an order from the shopping cart
- Store order history for each user

🔍 **Filtering and searching**
- Powered by **SpecificationProvider** and  **SpecificationBuilder**
- Dynamic filtering and extendable architecture

✅ **Validation**
- Spring Validation annotations on all DTOs 

🔐 **Security**
- JWT authentication
- Role-based access per endpoint
- Service-level permission checks

🧪 **Testing**
- Mocking-based service tests
- Integration tests simulating real workflows


Overall, this section  demonstrates how the back-end supports a full shopping experience:
**`register → login → browse catalog → add books → manage shopping cart → place order`**


## 🏗 Architecture & Technology Stack
This project is designed with **production-grade architecture** in mind.  
It follows a **layered architecture** pattern to separate concerns and ensure maintainability:

- **Presentation Layer / Controller**: Handles HTTP requests and responses. Implements RESTful endpoints.
- **Service Layer**: Contains business logic, validation, and transaction management.
- **Repository Layer**: Interacts with the database using Spring Data JPA.
- **DTO Layer**: Transfers data between layers and ensures a clean API contract.
- **SpecificationProvider & SpecificationBuilder**: Enable dynamic filtering and searching of books.
- **Security Layer**: Handles authentication (JWT) and role-based authorization.
- **Exception Handling**: Custom exceptions with global handlers for predictable API responses.
- **Testing Layer(Spring Boot Testing)**: Ensures application reliability and correctness through mock and integration tests.


## Technology Stack
| Technology / Tool               | Purpose                                            |
|---------------------------------|----------------------------------------------------|
| Java 17                         | Core programming language                          |
| Spring Boot 3.2                 | Application framework                              |
| Spring Security + JWT           | Authentication & authorization                     |
| MySQL                           | Database                                           |
| Spring Data JPA                 | ORM / repository abstraction                       |
| Validation (Spring Validation)  | DTO input validation                               |
| Liquibase                       | Database migration & versioning                    |
| Mocking & Integration Testing   | Ensuring code correctness and workflow simulation  |
| SpecificationProvider & Builder | Dynamic filtering/search in catalog                |
| Repository Pattern              | Clean separation of data access logic              |
| Layered Architecture            | Maintainable, scalable system                      |
| Spring Boot Testing             | Mock and integration testing of application layers |


## UML Diagram  
![UML Diagram](UML-diagram-bookstore.drawio.png)

## 🛠 Local Setup / Getting Started
Follow these steps to run the BookStore application locally on your machine.
 
### 1. Prerequisites
Make sure you have the following software installed:

- **Java 19.0.1** ([Java](https://www.oracle.com/java/technologies/javase/jdk19-archive-downloads.html))
- **Maven 3.x** ([Maven](https://maven.apache.org/download.cgi))
- **MySQL 8.3** ([MySQL](https://dev.mysql.com/downloads/mysql/))
- Optionally, a tool to view Swagger UI (browser)

### 2. Clone the Repository  
 ```bash  
  git clone https://github.com/VitaliyProgrammer/bookstore.git
  cd bookstore
  ```
### 3. Configure the DataBase
Create a MySQL database: 
```bash
CREATE DATABASE bookstore_db
```
                                                   
<br>Update the ```bash src/main/resources/application.properties```: 
```bash
spring.datasource.url=jdbc:mysql://localhost:3306/bookstore_db
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD   
spring.jpa.hibernate.ddl-auto=update
```
The project uses Spring Boot 3.2.4, Hibernate 6.4.4 and Liquibase 4.24.0
for database management and migrations.

## 4. Build and Run the Application
```bash
mvn clean install  
mvn spring-boot:run
```
The application will start on:  
```bash
http://localhost:8080
```
## 📘 API Documentation
All endpoints are documented in Swagger UI:
[Open Swagger UI](http://localhost:8080/swagger-ui/index.html)


## 🎥 Video Presentation
A short video walk through of the BookStore application, including authentication
Swagger API demonstration, and order workflow, is available here:

👉 https://www.loom.com/share/0042f5def4ba49178c5b711cf35212fc


## 📌 Final Notes
This project reflects my approach to back-end engineering: thoughtful architecture,
clear separation of concerns, security-first design, and realistic workflows. It is
intended as both a learning milestone and a foundation for future production-ready systems.