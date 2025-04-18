# SpringBootEndterm Project 

This project is a microservices-based application built with Spring Boot. It includes separate services for `Order` and `Delivery`, integrated via Kafka. The project uses PostgreSQL for persistence and Flyway for database migrations.

---

##  Modules

- `Order`: Handles order creation and communication with the Delivery service.
- `Delivery`: Listens for order events and schedules deliveries.
- `docker`: Contains the Kafka and Zookeeper setup via Docker Compose.

---

##  Setup Instructions

###  Prerequisites
- Docker + Docker Compose
- Java 17
- Maven
- PostgreSQL installed and running

---

##  Setup Database

### 1. Create databases (drop schemas if they exist):

```postgresql
CREATE DATABASE order_db;
CREATE DATABASE delivery_db;
```

### 2. Create development user:

```postgresql
CREATE USER dev_user WITH PASSWORD 'secret123';
ALTER ROLE dev_user WITH SUPERUSER;
```

>  Flyway will run automatically on application startup using the SQL files from:
```
delivery/src/main/resources/db/migration
```

---

## 🛰 Start Kafka & Zookeeper

From the `docker/` directory:

```bash
docker-compose up
```

---

##  Running the Services

Navigate into each service (`Order`, `Delivery`) and run one of the following:

With Maven:
```bash
mvn spring-boot:run
```

With Maven Wrapper:
```bash
./mvnw spring-boot:run
```

---

##  Tech Stack

- Java 17
- Spring Boot 3.4.4
- Apache Kafka
- PostgreSQL
- Flyway (DB migrations)
- Docker + Docker Compose

---

##  .gitignore Notes

The `.gitignore` is configured to exclude:
- `target/` directories
- IntelliJ `.idea/`, `.iml` files
- Maven wrapper binaries
- Logs, OS-generated files, `.DS_Store`

---

✅ You're good to go! Run it, test it, ship it! ✨
