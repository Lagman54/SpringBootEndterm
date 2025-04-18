package com.example.Order.model.commands;

public record CreateDeliveryCommand(Long orderId, Long customerId, String address) {
}
