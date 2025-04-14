package com.example.Order.models;

import java.math.BigDecimal;

public class OrderEvent {
    private Long orderId;
    private BigDecimal price;
    private int quantity;

    public OrderEvent() {
    }

    public OrderEvent(Long orderId, BigDecimal price, int quantity) {
        this.orderId = orderId;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
