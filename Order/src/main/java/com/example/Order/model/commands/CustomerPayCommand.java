package com.example.Order.model.commands;

public record CustomerPayCommand(Long customerId, Long orderId, String productName, Double orderTotal){
}
