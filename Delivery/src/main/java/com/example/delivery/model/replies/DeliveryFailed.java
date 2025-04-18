package com.example.delivery.model.replies;

public record DeliveryFailed(Long orderId, Long CustomerId) implements DeliveryResult {
}
