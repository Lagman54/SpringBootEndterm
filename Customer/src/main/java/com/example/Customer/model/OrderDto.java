package com.example.Customer.model;

public record OrderDto(Long customerId, Long orderId, String productName, Double orderTotal) {
}