package com.example.Customer.model.replies;

public record CustomerNotFound(Long customerId, Long orderId) implements CustomerPaymentResult {
}
