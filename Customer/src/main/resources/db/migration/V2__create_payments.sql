CREATE TABLE payment (
                         id SERIAL PRIMARY KEY,
                         order_id BIGINT,
                         amount DOUBLE PRECISION,
                         status VARCHAR(50),
                         customer_id BIGINT NOT NULL,
                         CONSTRAINT fk_customer
                             FOREIGN KEY (customer_id)
                                 REFERENCES customers(id)
                                 ON DELETE CASCADE
);
