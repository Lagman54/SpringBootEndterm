CREATE TABLE delivery_history
(
    id         SERIAL PRIMARY KEY,
    start_time TIME NOT NULL,
    end_time   TIME NOT NULL,
    order_id BIGINT NOT NULL
);
