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

    @Column(name = "retry_time", insertable = false)
    private Instant retry_time;

    private boolean is_sent;

    public Instant getRetry_time() {
        return retry_time;
    }

    public void setRetry_time(Instant retry_time) {
        if (retry_time != null) {
            this.retry_time = retry_time;
        } else {
            throw new IllegalArgumentException("retry_time is null");
        }
    }

    public boolean isIs_sent() {
        return is_sent;
    }

    public void setIs_sent(boolean is_sent) {
        this.is_sent = is_sent;
    }

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

    public int getShardKey() {
        return shardKey;
    }

    public void setShardKey(int shardKey) {
        this.shardKey = shardKey;
    }
}
