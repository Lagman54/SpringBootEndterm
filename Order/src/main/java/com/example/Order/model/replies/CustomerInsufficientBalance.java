package com.example.Order.model.replies;

public record CustomerInsufficientBalance(Long orderId, Long customerId, Long requiredAmount, Long actualBalance) implements CustomerPaymentResult {
}
