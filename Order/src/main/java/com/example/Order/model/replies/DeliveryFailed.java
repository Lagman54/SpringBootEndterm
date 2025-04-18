package com.example.Order.model.replies;

public record DeliveryFailed(Long orderId, Long CustomerId) implements DeliveryResult {
}
