package com.example.Customer.service;

import com.example.Customer.model.OrderDto;

public interface PaymentService {
    void processPayment(OrderDto order);
}
