package com.example.Order.model.replies;

public record CustomerPaymentSuccess(Long orderId, Long customerId) implements CustomerPaymentResult {
}
