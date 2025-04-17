package com.example.Order.model.replies;

public record CustomerNotFound(Long customerId, Long orderId) implements CustomerPaymentResult {}