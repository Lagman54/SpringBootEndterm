package com.example.Order.outbox;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxRecord, Long> {


    @Query(value = """
        SELECT * FROM outbox
        WHERE shard_key = :shardKey
          AND id > :lastProcessedId
          AND created_at < now() - interval '5 seconds'
        ORDER BY id
        LIMIT 20
    """, nativeQuery = true)
    List<OutboxRecord> pollShard(
            @Param("shardKey") int shardKey,
            @Param("lastProcessedId") long lastProcessedId
    );

    @Transactional
    @Modifying
    @Query("""
        UPDATE OutboxRecord o
        SET o.processedAt = CURRENT_TIMESTAMP
        WHERE o.id IN :ids
    """)
    void markAsProcessed(@Param("ids") List<Long> ids);

//
//    private final NamedParameterJdbcTemplate jdbcTemplate;
//    private final ObjectMapper objectMapper;
//
//    public OutboxRepository(NamedParameterJdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
//        this.jdbcTemplate = jdbcTemplate;
//        this.objectMapper = objectMapper;
//    }
//
//    public void insert(OutboxRecord record) {
//        String sql = """
//            INSERT INTO outbox (message_type, payload, shard_key)
//            VALUES (:message_type, CAST(:payload AS jsonb), :shard_key)
//        """;
//
//        MapSqlParameterSource params = null;
//        try {
//            params = new MapSqlParameterSource()
//                    .addValue("message_type", record.getMessageType().name())
//                    .addValue("payload", objectMapper.writeValueAsString(record.getPayload()))
//                    .addValue("shard_key", record.getShardKey());
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//
//        jdbcTemplate.update(sql, params);
//    }
//
//    public List<OutboxRecord> pollShard(int shard, long lastProcessedId) {
//        String sql = """
//            SELECT * FROM outbox
//            WHERE shard_key = :shard_key
//              AND id > :last_processed_id
//              AND created_at < now() - interval '5 seconds'
//            ORDER BY id
//            LIMIT 20
//        """;
//
//        MapSqlParameterSource params = new MapSqlParameterSource()
//                .addValue("shard_key", shard)
//                .addValue("last_processed_id", lastProcessedId);
//
//        return jdbcTemplate.query(
//                sql,
//                params,
//                new BeanPropertyRowMapper<>(OutboxRecord.class)
//        );
//    }
//
//    public void markAsProcessed(List<Long> ids) {
//        if (ids.isEmpty()) return;
//
//        String sql = "UPDATE outbox SET processed_at = now() WHERE id IN (:ids)";
//
//        MapSqlParameterSource params = new MapSqlParameterSource();
//        params.addValue("ids", ids);
//
//        jdbcTemplate.update(sql, params);
//    }
}
