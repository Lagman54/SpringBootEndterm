package com.example.Order.model.replies;

public record CustomerInsufficientBalance(Long customerId) implements CustomerPaymentResult {}
