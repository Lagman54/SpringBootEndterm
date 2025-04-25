package com.example.delivery.service;

import com.example.delivery.model.DeliveryResultConverter;
import com.example.delivery.model.OutboxEvent;
import com.example.delivery.model.replies.DeliveryResult;
import com.example.delivery.repository.OutboxRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class OutboxPublisher {
    private static final Logger log = LogManager.getLogger(OutboxPublisher.class);

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, DeliveryResult> paymentKafka;
    private final DeliveryResultConverter converter;

    @Value("${app.kafka.delivery-result-topic}")
    private String deliveryResultTopic;

    public OutboxPublisher(
            OutboxRepository outboxRepository,
            KafkaTemplate<String, DeliveryResult> paymentKafka,
            DeliveryResultConverter converter
    ) {
        this.outboxRepository = outboxRepository;
        this.paymentKafka     = paymentKafka;
        this.converter        = converter;
    }

    @Scheduled(fixedDelay = 1_000)
    public void publishOutboxEvents() {
        List<OutboxEvent> events = outboxRepository.findAllBySentFalse();

        for (OutboxEvent ev : events) {
            DeliveryResult result = converter.convert(ev.getType(), ev.getPayload());
            CompletableFuture<SendResult<String, DeliveryResult>> future = paymentKafka.send(deliveryResultTopic, result);
            future.thenAccept(r -> {
                log.info("Message was sent: {}", result);
                ev.setSent(true);
                outboxRepository.save(ev);
            }).exceptionally(ex -> {
                log.info("Message failed to send: {}, Error: ", result, ex);
                return null;
            });
        }
    }
}
