ALTER TABLE delivery_slots
    ADD COLUMN IF NOT EXISTS max_quantity INT NOT NULL DEFAULT 5;

-- Add 'customer_id' column to delivery_history
ALTER TABLE delivery_history
    ADD COLUMN IF NOT EXISTS customer_id BIGINT;