package com.example.Order.listener;

import com.example.Order.model.RejectionReason;
import com.example.Order.model.replies.*;
import com.example.Order.service.OrderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
public class KafkaEventsListener {
    private static final Logger log = LogManager.getLogger(KafkaEventsListener.class);

    private final OrderService orderService;

    public KafkaEventsListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "${app.kafka.payment-result-topic}",
            groupId = "${app.kafka.customer-payment-result-group}",
            containerFactory = "customerPaymentResultListenerFactory"
    )
    public void listen(CustomerPaymentResult result) {
        if (result instanceof CustomerNotFound notFound) {
            log.info("Customer not found: customerID:{}, orderID:{}", notFound.customerId(), notFound.orderId());
            orderService.rejectOrder(notFound.orderId(), RejectionReason.UNKNOWN_CUSTOMER);
        } else if (result instanceof CustomerInsufficientBalance insufficient) {
            log.info("Insufficient balance: {}", insufficient.customerId());
            orderService.rejectOrder(insufficient.orderId(), RejectionReason.INSUFFICIENT_CREDIT);
        } else if (result instanceof CustomerPaymentSuccess paymentResult) {
            log.info("Order was paid. OrderID: {}. CustomerID: {}", paymentResult.orderId(), paymentResult.customerId());
            orderService.createDelivery(paymentResult.orderId());
        }
    }

    @KafkaListener(topics = "${app.kafka.delivery-result-topic}",
            groupId = "${app.kafka.delivery-result-group}",
            containerFactory = "deliveryResultListenerFactory"
    )
    public void listen(DeliveryResult result) {
        if (result instanceof DeliveryFailed fail) {
            log.info("Delivery failed: orderID{}", fail.orderId());
            orderService.rejectOrder(fail.orderId(), RejectionReason.DELIVERY_FAILED);
        } else if (result instanceof DeliverySuccess deliveryResult) {
            log.info("Order was shipped. OrderID: {}. CustomerID: {}", deliveryResult.orderId(), deliveryResult.customerId());
            orderService.approveOrder(deliveryResult.orderId());
        }
    }

}