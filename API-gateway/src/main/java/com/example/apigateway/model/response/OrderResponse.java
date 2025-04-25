package com.example.apigateway.model.response;

public class OrderResponse {
    private Long id;
    private String productName;
    private Double orderTotal;
    private Long customerId;

    public OrderResponse() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setOrderTotal(Double orderTotal) {
        this.orderTotal = orderTotal;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getProductName() {
        return productName;
    }

    public Double getOrderTotal() {
        return orderTotal;
    }

    public Long getCustomerId() {
        return customerId;
    }
}
