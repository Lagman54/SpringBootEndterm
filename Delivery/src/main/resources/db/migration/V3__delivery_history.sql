CREATE TABLE delivery_history (
          id          SERIAL PRIMARY KEY,
          order_id    BIGINT NOT NULL,
          customer_id BIGINT,
          slot_id     BIGINT,
          start_time  TIME NOT NULL,
          end_time    TIME NOT NULL
);
