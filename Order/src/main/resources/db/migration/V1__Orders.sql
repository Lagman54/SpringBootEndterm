CREATE TABLE IF NOT EXISTS orders
(
    id           SERIAL PRIMARY KEY,
    product_name VARCHAR(255),
    quantity     INT,
    price        NUMERIC(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);