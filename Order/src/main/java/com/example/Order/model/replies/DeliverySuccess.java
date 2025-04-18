package com.example.Order.model.replies;

public record DeliverySuccess(Long orderId, Long customerId) implements DeliveryResult {
}
