package com.example.Customer.model;

import com.example.Customer.model.replies.CustomerInsufficientBalance;
import com.example.Customer.model.replies.CustomerNotFound;
import com.example.Customer.model.replies.CustomerPaymentResult;
import com.example.Customer.model.replies.CustomerPaymentSuccess;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class PaymentResultConverter {

    private final ObjectMapper objectMapper;

    public PaymentResultConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public CustomerPaymentResult convert(String type, String payload) {
        try {
            return switch (type) {
                case "customer.payment.success"        -> objectMapper.readValue(payload, CustomerPaymentSuccess.class);
                case "customer.payment.insufficient"  -> objectMapper.readValue(payload, CustomerInsufficientBalance.class);
                case "customer.payment.not_found"     -> objectMapper.readValue(payload, CustomerNotFound.class);
                default -> throw new IllegalArgumentException("Unknown payment-result type: " + type);
            };
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize OutboxEvent payload", e);
        }
    }
}