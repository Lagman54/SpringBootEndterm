package com.example.Order.outbox;

import com.example.Order.model.Order;
import com.example.Order.model.commands.CreateDeliveryCommand;
import com.example.Order.model.commands.CustomerPayCommand;
import com.example.Order.model.replies.CustomerPaymentResult;
import com.example.Order.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class MessageProducer {
    private static final Logger log = LogManager.getLogger(MessageProducer.class);

    @Value("${app.kafka.order-created-topic}")
    private String paymentTopic;

    @Value("${app.kafka.delivery-created-topic}")
    private String deliveryTopic;


    private final KafkaTemplate<String, CustomerPayCommand> payCommandKafkaTemplate;
    private final KafkaTemplate<String, CreateDeliveryCommand> deliveryCommandKafkaTemplate;

    public MessageProducer(KafkaTemplate<String, CustomerPayCommand> payCommandKafkaTemplate, KafkaTemplate<String, CreateDeliveryCommand> deliveryCommandKafkaTemplate) {
        this.payCommandKafkaTemplate = payCommandKafkaTemplate;
        this.deliveryCommandKafkaTemplate = deliveryCommandKafkaTemplate;
    }

    public CompletableFuture<?> send(OutboxRecord record) {

        log.info("trying to send message from outbox: {}", record);
        CompletableFuture<?> future;
        ObjectMapper mapper = new ObjectMapper();
        switch(record.getMessageType()) {
            case ORDER_CREATED:
                try {
                    CustomerPayCommand payCommand = mapper.readValue(record.getPayload(), CustomerPayCommand.class);
                    future = payCommandKafkaTemplate.send(paymentTopic, payCommand);
                    future.thenAccept(result -> {
                        log.info("Order was sent for payment: {}", payCommand);
                    }).exceptionally(ex -> {
                        log.error("Failed payment: {}, Error: {}", payCommand.orderId(), ex.getMessage());
                        return null;
                    });
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                break;
            case CREATE_DELIVERY:
                try {
                    CreateDeliveryCommand deliveryCommand = mapper.readValue(record.getPayload(), CreateDeliveryCommand.class);
                    future = deliveryCommandKafkaTemplate.send(deliveryTopic, deliveryCommand);
                    future.thenAccept(result -> {
                        log.info("Delivery was sent: {}", deliveryCommand);
                    }).exceptionally(ex -> {
                        log.error("Delivery failed: {}, Error: {}", deliveryCommand, ex.getMessage());
                        return null;
                    });
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                throw new IllegalArgumentException("Message type not supported");
        }
        return future;
    }
}
