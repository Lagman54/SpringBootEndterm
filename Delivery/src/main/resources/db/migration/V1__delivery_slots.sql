CREATE TABLE delivery_slots (
    id SERIAL PRIMARY KEY,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    capacity INT NOT NULL CHECK (capacity >= 0),
    quantity INT NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    CHECK (quantity <= capacity)
);
