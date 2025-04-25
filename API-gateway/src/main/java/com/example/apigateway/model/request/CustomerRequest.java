package com.example.apigateway.model.request;

public class CustomerRequest {

    private String name;
    private Long balance = 1_000_000L;

    public CustomerRequest() {

    }

    public CustomerRequest(String name) {
        this.name = name;
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
