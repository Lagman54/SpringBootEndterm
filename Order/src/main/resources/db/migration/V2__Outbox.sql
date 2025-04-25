CREATE TABLE outbox
(
    id           BIGSERIAL PRIMARY KEY,
    message_type TEXT  NOT NULL,
    payload      JSONB NOT NULL,
    created_at   TIMESTAMPTZ DEFAULT now(),
    retry_time TIMESTAMPTZ DEFAULT now(),
    is_sent boolean DEFAULT false,
    shard_key    INT   NOT NULL
);

CREATE INDEX idx_outbox_polling
    ON outbox (shard_key, retry_time, id)
    WHERE is_sent = false;
