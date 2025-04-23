package com.example.Order.outbox;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnTransformer;

import java.time.Instant;

@Entity
@Table(name = "outbox")
public class OutboxRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @Column(columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String payload;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "processed_at")
    private Instant processedAt;


    private int shardKey;

    public OutboxRecord() {}

    public OutboxRecord(MessageType messageType, String payload, int shardKey) {}

    @Override
    public String toString() {
        return "OutboxRecord{" +
                "id=" + id +
                ", messageType=" + messageType +
                ", payload='" + payload + '\'' +
                ", createdAt=" + createdAt +
                ", processedAt=" + processedAt +
                ", shardKey=" + shardKey +
                '}';
    }

    public Long getId() {
        return id;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }

    public int getShardKey() {
        return shardKey;
    }

    public void setShardKey(int shardKey) {
        this.shardKey = shardKey;
    }
}
