package com.example.Order.outbox;

import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class OutboxProcessor {
    private final Logger log = LogManager.getLogger(OutboxProcessor.class);

    @Value("${app.outbox.totalShards:4}")
    private int totalShards;

    private final OutboxService outboxService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

    public OutboxProcessor(OutboxService outboxService) {
        this.outboxService = outboxService;
    }

//    @Scheduled(fixedRate = 1000)
//    public void reportCurrentTime() {
//        log.info("The time is now");
//        outboxService.processShard(0);
//    }

    @PostConstruct
    public void schedulePollers() {
        for (int shard = 0; shard < totalShards; shard++) {
            int finalShard = shard;
            scheduler.scheduleAtFixedRate(
                    () -> outboxService.processShard(finalShard),
                    0, 15, TimeUnit.SECONDS
            );
        }
    }

}
