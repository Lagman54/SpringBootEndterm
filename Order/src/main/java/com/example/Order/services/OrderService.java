package com.example.Order.services;


import com.example.Order.models.Order;
import com.example.Order.repo.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final String orderTopic = "order-events";

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        KafkaTemplate<String, String> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Order createOrder(Order order) {
        System.out.println("🔥 OrderService.createOrder() called");

        Order savedOrder = orderRepository.save(order);

        String eventMessage = String.format("ORDER_CREATED: %d", savedOrder.getId());
        kafkaTemplate.send(orderTopic, eventMessage);

        return savedOrder;
    }
}