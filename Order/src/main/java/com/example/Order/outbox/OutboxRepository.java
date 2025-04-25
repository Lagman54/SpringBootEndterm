package com.example.Order.outbox;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxRecord, Long> {


    @Query(value = """
                SELECT * FROM outbox
                WHERE shard_key = :shardKey
                  AND :currentTime > retry_time
                  AND is_sent = false
                ORDER BY id
                LIMIT 10
            """, nativeQuery = true)
    List<OutboxRecord> pollShard(
            @Param("shardKey") int shardKey,
            @Param("currentTime") Instant currentTime
    );

    @Transactional
    @Modifying
    @Query("""
                UPDATE OutboxRecord o
                SET o.is_sent = true
                WHERE o.id IN :ids
            """)
    void markAsProcessed(@Param("ids") List<Long> ids);

    @Modifying
    @Transactional
    @Query("UPDATE OutboxRecord o SET o.retry_time = :newTime WHERE o.id IN :ids")
    int setRetryTimeForIds(@Param("ids") List<Long> ids, @Param("newTime") Instant newTime);
}
