package com.example.delivery.service;

import com.example.delivery.model.DeliveryResultConverter;
import com.example.delivery.model.OutboxEvent;
import com.example.delivery.model.replies.DeliveryResult;
import com.example.delivery.repository.OutboxRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutboxPublisher {

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

    @Scheduled(fixedDelay = 5_000)
    public void publishOutboxEvents() {
        List<OutboxEvent> events = outboxRepository.findAllBySentFalse();

        for (OutboxEvent ev : events) {
            DeliveryResult result = converter.convert(ev.getType(), ev.getPayload());
//            CustomerPaymentSuccess result = (CustomerPaymentSuccess) converter.convert(ev.getType(), ev.getPayload());
            paymentKafka.send(deliveryResultTopic, result);
            ev.setSent(true);
        }

        outboxRepository.saveAll(events);
    }
}
