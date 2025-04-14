# 🛠️ Spring Boot Endterm Project

This project demonstrates a microservices architecture using **Spring Boot**, **Kafka**, **PostgreSQL**, and **Docker**. It includes two main services: **Order Service** and **Customer Service**, connected via Kafka using the **Transactional Outbox Pattern**.

---

## ✅ Prerequisites

Ensure the following are installed on your machine:

- Java 17 or higher  
- Maven  
- PostgreSQL  
- Docker & Docker Compose  
- Git  

---


Access PostgreSQL CLI:

psql -U postgres

Create required databases:

CREATE DATABASE order_db;
CREATE DATABASE customer_db;

Create orders table in order_db:

\c order_db
CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    product_name VARCHAR(255),
    quantity INT,
    price NUMERIC(10,2)
);

Create payment table in customer_db:

\c customer_db
CREATE TABLE payment (
    id SERIAL PRIMARY KEY,
    order_id BIGINT,
    amount NUMERIC(10,2),
    status VARCHAR(50)
);

exit:
\q

🚀 Start Kafka Using Docker Compose
1. Navigate to Kafka Docker directory:
   cd kafka-docker
2. Start Kafka services:
   docker-compose up -d


▶️ Run the Microservices
Each service is a standalone Spring Boot application.

1. Navigate to each service directory:

Terminal1:
cd order
mvn spring-boot:run


Terminal2
cd customer-service
mvn spring-boot:run



🧪 Testing the Application
With both services running:

Send a POST request to create a new order:

'''bash
curl -X POST http://localhost:8080/api/orders \
-H "Content-Type: application/json" \
-d '{"product_name":"Widget","quantity":10,"price":19.99}'



📘 Additional Notes
Ensure application.properties in both services are configured with:

src/main/resources/application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/customer_db
spring.datasource.username=postgres
spring.datasource.password=your_password   #Maybe Different

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect


