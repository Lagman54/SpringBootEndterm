✅ Step-by-Step Commands (Windows CMD) for dbs and tables
1. Open CMD and start psql
If psql is in your system PATH, run:
cmd
psql -U postgres
If it's not in PATH, use full path like:
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres
It will prompt for your password (enter the PostgreSQL postgres user password).
2. Create the order_db and customer_db databases
In the psql prompt:
CREATE DATABASE order_db;
CREATE DATABASE customer_db;
3. Connect to order_db and create the orders table
\c order_db
CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    product_name VARCHAR(255),
    quantity INT,
    price NUMERIC(10,2)
);
4. Connect to customer_db and create the payment table
\c customer_db
CREATE TABLE payment (
    id SERIAL PRIMARY KEY,
    order_id BIGINT,
    amount NUMERIC(10,2),
    status VARCHAR(50)
);
5. Exit psql
\q

✅ Step-by-Step Kafka Startup (Windows CMD)
cd "C:\Users\KogSa\OneDrive\Рабочий стол\предметы\spring\project_trial\kafka-docke
docker-compose up -d

to test start both order and customer apps and in cmd
curl -X POST http://localhost:8080/api/orders ^
  -H "Content-Type: application/json" ^
  -d "{\"productName\": \"Laptop\", \"quantity\": 1, \"price\": 1000.0}"

to check topics
docker exec -it kafka bash
kafka-topics --list --bootstrap-server localhost:9092

