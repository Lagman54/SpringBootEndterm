package com.example.Order.model.commands;

public record CreateDeliveryCommand(Long customerId, Long orderId, String address) {
}
