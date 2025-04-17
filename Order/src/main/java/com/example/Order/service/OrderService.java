package com.example.Order.service;

import com.example.Order.model.Order;
import com.example.Order.model.commands.CustomerPayCommand;
import com.example.Order.repository.OrderRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private static final Logger log = LogManager.getLogger(OrderService.class);

    @Value("${app.kafka.order-created-topic}")
    private String paymentTopic;

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, CustomerPayCommand> kafkaTemplate;

    public OrderService(OrderRepository orderRepository, KafkaTemplate<String, CustomerPayCommand> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Order createOrder(Order order) {
        log.info("Received request to order: {}", order);
        Order savedOrder = orderRepository.save(order);
        CustomerPayCommand command = new CustomerPayCommand(order.getCustomerId(), order.getId(), order.getOrderTotal());
        kafkaTemplate.send(paymentTopic, command);
        log.info("Order created and sent for payment: {}", savedOrder);
        return savedOrder;
    }
}