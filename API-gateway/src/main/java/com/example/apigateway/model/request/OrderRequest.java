package com.example.apigateway.model.request;

public class OrderRequest {
    private String productName;
    private Double orderTotal;

    public String getProductName() {
        return productName;
    }

    public Double getOrderTotal() {
        return orderTotal;
    }
}
