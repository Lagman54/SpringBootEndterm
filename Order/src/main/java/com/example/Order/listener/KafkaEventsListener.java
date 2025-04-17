package com.example.Order.listener;

import com.example.Order.model.replies.CustomerInsufficientBalance;
import com.example.Order.model.replies.CustomerNotFound;
import com.example.Order.model.replies.CustomerPaymentResult;
import com.example.Order.model.replies.CustomerPaymentSuccess;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaEventsListener {
    private static final Logger log = LogManager.getLogger(KafkaEventsListener.class);

    @KafkaListener(topics = "${app.kafka.payment-result-topic}",
            groupId = "${app.kafka.customer-payment-result-group}",
            containerFactory = "customerPaymentResultListenerFactory"
    )
    public void listen(CustomerPaymentResult result) {
        if (result instanceof CustomerNotFound notFound) {
            log.info("Customer not found: ID{}", notFound.customerId());
        } else if (result instanceof CustomerInsufficientBalance insufficient) {
            log.info("Insufficient balance: {}", insufficient.customerId());
        } else if (result instanceof CustomerPaymentSuccess paymentResult) {
            log.info("Order was paid. OrderID: {}. CustomerID: {}", paymentResult.orderId(), paymentResult.customerId());
        }
    }


}
