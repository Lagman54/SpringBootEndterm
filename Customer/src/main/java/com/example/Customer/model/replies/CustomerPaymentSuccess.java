package com.example.Customer.model.replies;

public record CustomerPaymentSuccess(Long orderId, Long customerId) implements CustomerPaymentResult {
}
