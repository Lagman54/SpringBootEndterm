package com.example.Order.outbox;

import com.example.Order.model.Order;
import com.example.Order.model.commands.CreateDeliveryCommand;
import com.example.Order.model.commands.CustomerPayCommand;
import com.example.Order.service.OrderService;
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
//        if(record.getMessageType() == MessageType.ORDER_CREATED) {
//            CustomerPayCommand command = new CustomerPayCommand(order.getCustomerId(), order.getId(), order.getOrderTotal());
//
//            log.info("Order created: {}. Trying to send for payment.", savedOrder);
//            CompletableFuture<SendResult<String, CustomerPayCommand>> future = payCommandKafkaTemplate.send(paymentTopic, command);
//            future.thenAccept(result -> {
//                log.info("Order was sent for payment: {}", savedOrder);
//            }).exceptionally(ex -> {
//                log.error("Failed sending orderID={} for payment: {}", order.getId(), ex.getMessage());
//                return null;
//            });
//        }


        // TODO map records to events
        log.info("send record: {}", record);
        CustomerPayCommand command = new CustomerPayCommand(2L, 76L, 100.0);
        CompletableFuture<SendResult<String, CustomerPayCommand>> future = payCommandKafkaTemplate.send(paymentTopic, command);
        return future;
    }
}
