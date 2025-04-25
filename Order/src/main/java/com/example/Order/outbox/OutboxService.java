package com.example.Order.outbox;

import com.example.Order.model.commands.CreateDeliveryCommand;
import com.example.Order.model.commands.CustomerPayCommand;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class OutboxService {
    private final Logger log = LogManager.getLogger(OutboxService.class);
    @Value("${app.kafka.order-created-topic}")
    private String paymentTopic;

    @Value("${app.kafka.delivery-created-topic}")
    private String deliveryTopic;

    @Value("${app.outbox.timeout}")
    private Integer timeoutInSeconds;

    private final KafkaTemplate<String, CustomerPayCommand> payCommandKafkaTemplate;
    private final KafkaTemplate<String, CreateDeliveryCommand> deliveryCommandKafkaTemplate;
    private final OutboxRepository repository;

    public OutboxService(OutboxRepository repository, KafkaTemplate<String, CustomerPayCommand> payCommandKafkaTemplate, KafkaTemplate<String, CreateDeliveryCommand> deliveryCommandKafkaTemplate) {
        this.repository = repository;
        this.payCommandKafkaTemplate = payCommandKafkaTemplate;
        this.deliveryCommandKafkaTemplate = deliveryCommandKafkaTemplate;
    }

    public void processShard(int shard) {
        try {
//            log.info("Trying to process shard: {}", shard);
            Instant currentTime = Instant.now();
            List<OutboxRecord> records = repository.pollShard(shard, currentTime);
//            log.info("Trying to process shard: {}, records: {}", shard, records);
            processRecords(records);
        } catch (Exception e) {
            log.error("Exception occurred in processing shard {}: {}", shard, e.getMessage());
        }
    }

    private void processRecords(List<OutboxRecord> records) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        List<OutboxRecord> successfulMessages = new ArrayList<>();
        List<OutboxRecord> failedMessages = new ArrayList<>();
        for (OutboxRecord record : records) {
            CompletableFuture<?> future;
            ObjectMapper mapper = new ObjectMapper();
            switch (record.getMessageType()) {
                case ORDER_CREATED:
                    try {
                        CustomerPayCommand payCommand = mapper.readValue(record.getPayload(), CustomerPayCommand.class);
                        log.info("try sending pay command: {}, {}", payCommand, Thread.currentThread().getName());
                        future = payCommandKafkaTemplate.send(paymentTopic, payCommand)
                                .thenAccept(result -> {
                                    log.info("Order was sent for payment: {}", payCommand);
                                    successfulMessages.add(record);
                                }).exceptionally(ex -> {
                                    log.error("Failed to send message: {}, Error: {}", payCommand.orderId(), ex.getMessage());
                                    failedMessages.add(record);
                                    return null;
                                });
                        futures.add(future);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case CREATE_DELIVERY:
                    try {
                        CreateDeliveryCommand deliveryCommand = mapper.readValue(record.getPayload(), CreateDeliveryCommand.class);
                        future = deliveryCommandKafkaTemplate.send(deliveryTopic, deliveryCommand)
                                .thenAccept(result -> {
                                    log.info("Delivery was sent: {}", deliveryCommand);
                                    successfulMessages.add(record);
                                }).exceptionally(ex -> {
                                    log.error("Delivery failed: {}, Error: {}", deliveryCommand, ex.getMessage());
                                    failedMessages.add(record);
                                    return null;
                                });
                        futures.add(future);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Message type not supported");
            }
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[futures.size()])).join();

        if (!successfulMessages.isEmpty()) {
            List<Long> ids = successfulMessages.stream().map(OutboxRecord::getId).toList();
            repository.markAsProcessed(ids);
        }

        if (!failedMessages.isEmpty()) {
            log.info("Setting new retry time for: {}", failedMessages);
            var currentTime = Instant.now();
            List<Long> ids = failedMessages.stream().map(OutboxRecord::getId).toList();
            repository.setRetryTimeForIds(ids, currentTime.plus(Duration.ofSeconds(timeoutInSeconds)));
        }

    }
}
