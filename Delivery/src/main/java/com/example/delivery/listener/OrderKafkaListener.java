package com.example.delivery.listener;

import com.example.delivery.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Component
@Service
@Slf4j
public class OrderKafkaListener {

    @RetryableTopic(attempts = "2")
    @KafkaListener(topics = "${app.kafka.orders-topic}",
            groupId = "${app.kafka.orders-groupId}",
            containerFactory = "orderKafkaListenerContainerFactory")
    public void listen(@Payload Order order,
                       @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) Long key,
                       @Header(value = KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(value = KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                       @Header(value = KafkaHeaders.RECEIVED_TIMESTAMP) Long timestamp) {
        log.info("Received order: {}", order);
        log.info("Key: {}; Partition: {}; Topic: {}; Timestamp: {}", key, partition, topic, timestamp);


    }

    @DltHandler
    public void dltListener(Order order,
                            @Header(value = KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Get message with error: {}. Topic: {}.", order, topic);
    }

}
