package com.example.Customer.listener;

import com.example.Customer.model.OrderDto;
import com.example.Customer.service.PaymentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaEventsListener {
    private static final Logger log = LogManager.getLogger(KafkaEventsListener.class);

    private final PaymentService paymentService;

    public KafkaEventsListener(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = "${app.kafka.order-created-topic}",
            groupId = "${app.kafka.order-created-consumers}",
            containerFactory = "orderCreatedListenerFactory"
    )
    public void listen(OrderDto order) {
        // order достаешь отсюда и передаешь его в сервис
        log.info("Received order: {}", order);
        paymentService.processPayment(order);
    }


}