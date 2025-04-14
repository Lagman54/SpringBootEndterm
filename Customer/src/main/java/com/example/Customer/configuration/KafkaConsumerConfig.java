package com.example.Customer.configuration;

import com.example.Customer.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    private final PaymentService paymentService;

    @Autowired
    public KafkaConsumerConfig(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = "order-events", groupId = "customer-service-group")
    public void listenOrderEvents(String message) {
        paymentService.processPaymentEvent(message);
    }
}