# 🛠️ Spring Boot Endterm Project

This project demonstrates a **microservices architecture** using **Spring Boot**, **Kafka**, **PostgreSQL**, and **Docker**. It includes two main services:

- **Order Service**
- **Customer Service**

They are connected via Kafka using the **Transactional Outbox Pattern**.

---

## ✅ Prerequisites

Ensure the following tools are installed on your machine:

- Java 17 or higher  
- Maven  
- PostgreSQL  
- Docker & Docker Compose  
- Git  

---

## 🗄️ Set Up PostgreSQL

### Access PostgreSQL CLI:

```bash
psql -U postgres
```

### Create Required Databases:

```sql
CREATE DATABASE order_db;
CREATE DATABASE customer_db;
```

### Create `orders` Table in `order_db`:

```sql
\c order_db
CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    product_name VARCHAR(255),
    quantity INT,
    price NUMERIC(10,2)
);
```

### Create `payment` Table in `customer_db`:

```sql
\c customer_db
CREATE TABLE payment (
    id SERIAL PRIMARY KEY,
    order_id BIGINT,
    amount NUMERIC(10,2),
    status VARCHAR(50)
);
```

### Exit PostgreSQL:

```sql
\q
```

---

## 🐳 Start Kafka Using Docker Compose

```bash
cd kafka-docker
docker-compose up -d
```

Ensure that Kafka and Zookeeper containers are running successfully.

---

## ▶️ Run the Microservices

Each service is a standalone Spring Boot application.

### 🧵 Terminal 1 — Run Order Service:

```bash
cd order
mvn spring-boot:run
```

### 🧵 Terminal 2 — Run Customer Service:

```bash
cd customer-service
mvn spring-boot:run
```

---

## 🧪 Testing the Application

Once both services are running, send a POST request to create a new order:

```bash
curl -X POST http://localhost:8080/api/orders \
-H "Content-Type: application/json" \
-d '{"product_name":"Widget","quantity":10,"price":19.99}'
```

Expected outcome:

- A new order is created
- A corresponding payment is triggered and saved in `customer_db` via Kafka

---

## 📘 Additional Notes

Ensure that `application.properties` in both services contain the following configuration (with respective database names):

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/customer_db
spring.datasource.username=postgres
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

Replace `customer_db` with `order_db` in the Order Service.

---

## ✅ Summary

- Two Spring Boot services: Order & Customer  
- PostgreSQL for persistence  
- Kafka for async communication  
- Docker Compose for Kafka setup  
- Transactional Outbox pattern for reliability  
