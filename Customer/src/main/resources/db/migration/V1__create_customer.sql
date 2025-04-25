CREATE TABLE customers
(
    id      SERIAL PRIMARY KEY,
    name    VARCHAR(255),
    balance BIGINT,
    username varchar(255) UNIQUE NOT NULL,
    password varchar(255) NOT NULL
);
