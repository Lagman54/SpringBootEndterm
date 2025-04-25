CREATE TABLE outbox_event
(
    id             UUID PRIMARY KEY,
    aggregate_type VARCHAR(100),
    aggregate_id   VARCHAR(100),
    type           VARCHAR(100),
    payload        TEXT NOT NULL,
    created_at     TIMESTAMP DEFAULT now(),
    sent           BOOLEAN   DEFAULT false
);

