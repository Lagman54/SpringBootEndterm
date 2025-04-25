package com.example.Customer.service;

import com.example.Customer.model.Customer;
import com.example.Customer.model.OrderDto;

public interface CustomerService {
    Customer createCustomer(Customer customer);

    Customer findByUsername(String username);

    boolean checkPassword(Customer c, String rawRawPassword);

    void processPayment(OrderDto order);

    Customer updateCustomer(Long id, String name, Long balance);
}
