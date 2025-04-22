CREATE TABLE delivery_history
(
    id          BIGSERIAL PRIMARY KEY,
    order_id    BIGINT,
    customer_id BIGINT,
    slot_id     BIGINT,
    start_time  TIME NOT NULL,
    end_time    TIME NOT NULL
);
