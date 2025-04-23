package com.example.Order.service;

import com.example.Order.model.Order;
import com.example.Order.model.RejectionReason;
import com.example.Order.model.commands.CreateDeliveryCommand;
import com.example.Order.model.commands.CustomerPayCommand;
import com.example.Order.outbox.MessageType;
import com.example.Order.outbox.OutboxRecord;
import com.example.Order.outbox.OutboxRepository;
import com.example.Order.outbox.ShardUtils;
import com.example.Order.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Service
public class OrderService {
    private static final Logger log = LogManager.getLogger(OrderService.class);

    @Value("${app.kafka.order-created-topic}")
    private String paymentTopic;

    @Value("${app.kafka.delivery-created-topic}")
    private String deliveryTopic;

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, CustomerPayCommand> payCommandKafkaTemplate;
    private final KafkaTemplate<String, CreateDeliveryCommand> deliveryCommandKafkaTemplate;
    private final ObjectMapper objectMapper;
    private final ShardUtils shardUtils;

    public OrderService(OrderRepository orderRepository, KafkaTemplate<String, CustomerPayCommand> kafkaTemplate, OutboxRepository outboxRepository, KafkaTemplate<String, CustomerPayCommand> payCommandKafkaTemplate, KafkaTemplate<String, CreateDeliveryCommand> deliveryCommandKafkaTemplate, ObjectMapper objectMapper, ShardUtils shardUtils) {
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
        this.payCommandKafkaTemplate = payCommandKafkaTemplate;
        this.deliveryCommandKafkaTemplate = deliveryCommandKafkaTemplate;
        this.objectMapper = objectMapper;
        this.shardUtils = shardUtils;
    }

    @Transactional
    public Order createOrder(Order order) {
        Order savedOrder = orderRepository.save(order);
        CustomerPayCommand command = new CustomerPayCommand(order.getCustomerId(), order.getId(), order.getOrderTotal());

        OutboxRecord record = new OutboxRecord();
        record.setMessageType(MessageType.ORDER_CREATED);

        try {
            record.setPayload(objectMapper.writeValueAsString(command));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize CreatePayCommand", e);
        }

        record.setShardKey(shardUtils.calculateShard(order.getId()));

        outboxRepository.save(record);
//        CustomerPayCommand command = new CustomerPayCommand(order.getCustomerId(), order.getId(), order.getOrderTotal());
//
//        log.info("Order created: {}. Trying to send for payment.", savedOrder);
//        CompletableFuture<SendResult<String, CustomerPayCommand>> future = payCommandKafkaTemplate.send(paymentTopic, command);
//        future.thenAccept(result -> {
//            log.info("Order was sent for payment: {}", savedOrder);
//        }).exceptionally(ex -> {
//            log.error("Failed sending orderID={} for payment: {}", order.getId(), ex.getMessage());
//            return null;
//        });
        return savedOrder;
    }

    public void rejectOrder(Long orderId, RejectionReason rejectionReason) {
        Order order = orderRepository.findById(orderId).get();
        order.reject(rejectionReason);
        orderRepository.save(order);
    }

    public void approveOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).get();
        order.approve();
        orderRepository.save(order);
    }

    public void createDelivery(Long orderId) {
        Order order = orderRepository.findById(orderId).get();
        CreateDeliveryCommand command = new CreateDeliveryCommand(order.getCustomerId(), order.getId(), "TODO");

        log.info("Trying to send for delivery: orderID={}", order.getId());
        CompletableFuture<SendResult<String, CreateDeliveryCommand>> future = deliveryCommandKafkaTemplate.send(deliveryTopic, command);
        future.thenAccept(result -> {
            log.info("Order was sent for delivery: orderID={}", orderId);
        }).exceptionally(ex -> {
            log.error("Failed sending orderID={} for delivery: {}", orderId, ex.getMessage());
            return null;
        });
    }
}