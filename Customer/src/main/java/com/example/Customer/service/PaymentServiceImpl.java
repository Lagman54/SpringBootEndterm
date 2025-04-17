package com.example.Customer.service;


import com.example.Customer.model.OrderDto;
import com.example.Customer.model.replies.CustomerPaymentResult;
import com.example.Customer.repo.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${app.kafka.payment-result-topic}")
    private String paymentResultTopic;

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, CustomerPaymentResult> kafkaTemplate;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, KafkaTemplate<String, CustomerPaymentResult> kafkaTemplate) {
        this.paymentRepository = paymentRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void processPayment(OrderDto order) {
        /* TODO оплатить заказ и отправить соответсвующий message в kafka
            1) Если заказ оплачен успеншо ->
            kafkaTemplate.send(paymentResultTopic, new CustomerPaymentSuccess(orderId, customerId));
            2) Если customer с данным id не найден ->
            kafkaTemplate.send(paymentResultTopic, new CustomerNotFound(customerId, orderId));
            3) Если у customer не хватает баланса ->
            kafkaTemplate.send(paymentResultTopic, new CustomerInsufficientBalance(orderId, customerId, requiredAmount, actualBalance));
         */

    }
}