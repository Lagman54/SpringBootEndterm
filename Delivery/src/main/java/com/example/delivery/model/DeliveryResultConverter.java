package com.example.delivery.model;

import com.example.delivery.model.replies.DeliveryFailed;
import com.example.delivery.model.replies.DeliveryResult;
import com.example.delivery.model.replies.DeliverySuccess;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

;

@Component
public class DeliveryResultConverter {

    private final ObjectMapper objectMapper;

    public DeliveryResultConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public DeliveryResult convert(String type, String payload) {
        try {
            return switch (type) {
                case "delivery.success"        -> objectMapper.readValue(payload, DeliverySuccess.class);
                case "delivery_failed"     -> objectMapper.readValue(payload, DeliveryFailed.class);
                default -> throw new IllegalArgumentException("Unknown payment-result type: " + type);
            };
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize OutboxEvent payload", e);
        }
    }
}