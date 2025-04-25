package com.example.apigateway.model.request;

public class CustomerRequest {

    private final String name;
    private Long balance = 1_000_000L;

    public CustomerRequest(String name) {
        this.name = name;
        this.balance = 1_000_000L;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public Long getBalance() {
        return balance;
    }
}
