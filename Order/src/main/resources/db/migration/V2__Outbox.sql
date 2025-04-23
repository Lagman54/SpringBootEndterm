CREATE TABLE outbox
(
    id           BIGSERIAL PRIMARY KEY,
    message_type TEXT  NOT NULL,
    payload      JSONB NOT NULL,
    created_at   TIMESTAMPTZ DEFAULT now(),
    processed_at TIMESTAMPTZ,
    shard_key    INT   NOT NULL
);

CREATE TABLE outbox_offsets
(
    shard_key         INT PRIMARY KEY,
    last_processed_id BIGINT      NOT NULL DEFAULT 0,
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

INSERT INTO outbox_offsets (shard_key) VALUES (0), (1), (2), (3);

CREATE INDEX idx_outbox_polling
    ON outbox (shard_key, id, created_at);

CREATE INDEX idx_outbox_rescue
    ON outbox (created_at)
    WHERE processed_at IS NULL;
