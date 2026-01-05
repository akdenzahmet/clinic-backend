# Clinic Backend (Spring Boot)

A Spring Boot REST API for a clinic management panel.

## Features
- JWT Authentication (admin users)
- Patients CRUD
- Appointments CRUD
- Tooth Treatments per patient (done / pending)
- PostgreSQL + Flyway migrations
- Meaningful API error responses (Global Exception Handler)

## Tech Stack
- Java + Spring Boot
- PostgreSQL
- Flyway
- Maven

## Run Locally

### 1) Start PostgreSQL
Create a database, e.g. `clinic_db`.

### 2) Configure application properties
Create a local config file (do not commit it):
`src/main/resources/application-local.properties`

Example:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/clinic_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true
