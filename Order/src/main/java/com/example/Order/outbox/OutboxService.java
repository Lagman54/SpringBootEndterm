package com.example.Order.outbox;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class OutboxService {
    private final Logger log = LogManager.getLogger(OutboxService.class);

    private final OutboxRepository repository;
    private final OutboxOffsetRepository offsetRepository;
    private final MessageProducer messageProducer;

    public OutboxService(OutboxRepository repository, OutboxOffsetRepository offsetRepository, MessageProducer messageProducer) {
        this.repository = repository;
        this.offsetRepository = offsetRepository;
        this.messageProducer = messageProducer;
    }

    public void processShard(int shard) {
//        System.out.println("Processing shard " + shard + " on " + Thread.currentThread().getName());

        long lastId = offsetRepository.getLastProcessedId(shard);
        List<OutboxRecord> records = repository.pollShard(shard, lastId);

        List<CompletableFuture<?>> futures = new ArrayList<>();
        List<Throwable> throwables = new ArrayList<>();
        for (OutboxRecord record : records) {
            CompletableFuture<?> future = messageProducer.send(record);
            future.thenAccept(result -> {
                log.info("message sent: {}", record);
            }).exceptionally(ex -> {
                throwables.add(ex);
                return null;
            });
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();

        if(!throwables.isEmpty()) {
            throw new RuntimeException(throwables.get(0));
        }

        if (!records.isEmpty()) {
            List<Long> ids = records.stream().map(OutboxRecord::getId).toList();
            repository.markAsProcessed(ids);
            offsetRepository.updateLastProcessedId(shard, records.get(records.size() - 1).getId());
        }
    }

}
