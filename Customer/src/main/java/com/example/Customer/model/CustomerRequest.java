package com.example.Customer.model;

public class CustomerRequest {

    private String name;
    private Long balance;

    public CustomerRequest() {

    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getBalance() {
        return balance;
    }
}
