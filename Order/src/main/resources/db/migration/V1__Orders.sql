CREATE TABLE IF NOT EXISTS orders
(
    id               SERIAL PRIMARY KEY,
    product_name     TEXT             NOT NULL,
    order_total      DOUBLE PRECISION NOT NULL,
    customer_id      BIGINT           NOT NULL,
    state            TEXT             NOT NULL,
    rejection_reason TEXT
);