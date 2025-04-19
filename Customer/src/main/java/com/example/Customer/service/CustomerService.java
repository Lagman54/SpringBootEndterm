package com.example.Customer.service;

import com.example.Customer.model.Customer;
import com.example.Customer.model.OrderDto;

public interface CustomerService {
    void processPayment(OrderDto order);
    public Customer createCustomer(Customer customer);
}
