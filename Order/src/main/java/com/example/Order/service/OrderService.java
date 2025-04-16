package com.example.Order.service;


import com.example.Order.model.Order;
import com.example.Order.repo.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.kafka.orders-topic}")
    private String orderTopic;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        KafkaTemplate<String, String> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Order createOrder(Order order) {
        Order savedOrder = orderRepository.save(order);

        String eventMessage = String.format("ORDER_CREATED: %d", savedOrder.getId());
        kafkaTemplate.send(orderTopic, eventMessage);

        log.info("Order created: {}", savedOrder);
        return savedOrder;
    }
}