package com.example.Order.outbox;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OutboxOffsetRepository {
    private final JdbcTemplate jdbcTemplate;

    public OutboxOffsetRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long getLastProcessedId(int shard) {
        return jdbcTemplate.queryForObject(
                "SELECT last_processed_id FROM outbox_offsets WHERE shard_key = ?",
                Long.class,
                shard
        );
    }

    public void updateLastProcessedId(int shard, long newId) {
        jdbcTemplate.update(
                "UPDATE outbox_offsets SET last_processed_id = ?, updated_at = now() WHERE shard_key = ?",
                newId, shard
        );
    }
}
