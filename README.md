# Distributed E-Commerce Microservices System  
This is a backend application simulating an e-commerce-like system, composed of three independently deployable microservices:

- Order Service

- Payment Service

- Delivery Service

- API Gateway

These services interact via asynchronous messaging, using Apache Kafka as a message broker and implementing the Transactional Outbox Pattern to ensure data consistency and reliable communication.

# 🧱 Architecture Overview
The system is based on a microservice architecture with the following key components:

- API Gateway: User registration, authentication and request redirection

- Order Service: Handles order placement and validation.

- Payment Service: Processes payments once an order is placed.

- Delivery Service: Arranges delivery once payment is confirmed.

- Kafka: Facilitates asynchronous, event-driven communication between services.

- Transactional Outbox Pattern: Ensures atomicity between local database writes and event publication.


# 🛠️ Tech Stack
- Java / Spring Boot

- Apache Kafka

- PostgreSQL

- Flyway

- Docker / Docker Compose

- JWT

- Gatling (for load testing)

# 🔄 Workflow
User places an order → Order Service saves it and emits an OrderCreated event.

Payment Service receives the event and attempts to process the payment.

On success, it emits a PaymentConfirmed event.

Delivery Service listens for that event and schedules delivery.

Each step is resilient and fault-tolerant due to asynchronous communication and retry mechanisms.

# 💡 Key Features
Event-Driven Design: Loosely coupled services using Kafka events.

Data Consistency: Ensured by the Transactional Outbox Pattern — events are only published after a successful database transaction.

Scalable: Each service can scale independently.

Load Tested: Used Gatling to test system performance under load.

# Start all services with Docker Compose
docker-compose up --build  
Access application at:
~~~
localhost:8084  
~~~

# Swagger-ui
~~~
localhost:8084/swagger-ui.html
~~~

# 🧪 Testing
Gatling scenarios available in /order-gatling-test directory

