package com.example.delivery.model.replies;

public record DeliverySuccess(Long orderId, Long customerId) implements DeliveryResult {
}
