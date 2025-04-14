package com.example.Order.services;

import com.example.Order.models.Order;
import com.example.Order.repo.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.Order.models.OrderEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

        try {
            // ✅ Создаём DTO с преобразованием Double -> BigDecimal
            OrderEvent event = new OrderEvent(
                    savedOrder.getId(),
                    new BigDecimal(savedOrder.getPrice()).setScale(2, RoundingMode.HALF_UP), // ← ВАЖНО
                    savedOrder.getQuantity()
            );

            // ✅ Превращаем в JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(event);

            // ✅ Отправляем в Kafka
            kafkaTemplate.send(orderTopic, json);

            System.out.println("✅ Sent JSON to Kafka: " + json);

        } catch (Exception e) {
            System.err.println("❌ Failed to send Kafka message");
            e.printStackTrace();
        }

        return savedOrder;
    }
}