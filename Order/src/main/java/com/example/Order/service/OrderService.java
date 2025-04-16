package com.example.Order.service;


import com.example.Order.model.Order;
import com.example.Order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    @Value("${app.kafka.orders-topic}")
    private String orderTopic;

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Order> kafkaTemplate;

    public Order createOrder(Order order) {
        Order savedOrder = orderRepository.save(order);

        kafkaTemplate.send(orderTopic, savedOrder);

        log.info("Order created: {}", savedOrder);
        return savedOrder;
    }
}