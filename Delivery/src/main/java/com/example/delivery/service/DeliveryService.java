package com.example.delivery.service;

import com.example.delivery.model.OrderDto;

public interface DeliveryService {
    void scheduleDelivery(OrderDto order);
}
