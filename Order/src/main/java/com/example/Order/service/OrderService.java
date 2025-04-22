package com.example.Order.service;

import com.example.Order.model.Order;
import com.example.Order.model.RejectionReason;
import com.example.Order.model.commands.CreateDeliveryCommand;
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

    @Value("${app.kafka.delivery-created-topic}")
    private String deliveryTopic;

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, CustomerPayCommand> payCommandKafkaTemplate;
    private final KafkaTemplate<String, CreateDeliveryCommand> deliveryCommandKafkaTemplate;

    public OrderService(OrderRepository orderRepository, KafkaTemplate<String, CustomerPayCommand> kafkaTemplate, KafkaTemplate<String, CustomerPayCommand> payCommandKafkaTemplate, KafkaTemplate<String, CreateDeliveryCommand> deliveryCommandKafkaTemplate) {
        this.orderRepository = orderRepository;
        this.payCommandKafkaTemplate = payCommandKafkaTemplate;
        this.deliveryCommandKafkaTemplate = deliveryCommandKafkaTemplate;
    }

    public Order createOrder(Order order) {
        Order savedOrder = orderRepository.save(order);
        CustomerPayCommand command = new CustomerPayCommand(order.getCustomerId(), order.getId(), order.getOrderTotal());
        payCommandKafkaTemplate.send(paymentTopic, command);
        log.info("Order created and sent for payment: {}", savedOrder);
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
        deliveryCommandKafkaTemplate.send(deliveryTopic, command);
        log.info("Order sent for delivery confirmation: {}", order);
    }
}