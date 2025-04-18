package com.example.Customer.model.replies;

public record CustomerInsufficientBalance(Long orderId, Long customerId, Double requiredAmount, Long actualBalance) implements CustomerPaymentResult{
}
