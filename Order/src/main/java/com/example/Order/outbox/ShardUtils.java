package com.example.Order.outbox;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ShardUtils {

    @Value("${app.outbox.totalShards:4}")
    private int totalShards;

    public int calculateShard(long id) {
        return Math.toIntExact(id % totalShards);
    }
}
