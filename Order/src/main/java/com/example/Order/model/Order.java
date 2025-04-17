package com.example.Order.model;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
@Access(AccessType.FIELD)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;
    private Double orderTotal;
    private Long customerId;

    @Enumerated(EnumType.STRING)
    private OrderState state = OrderState.PENDING;

    @Enumerated(EnumType.STRING)
    private RejectionReason rejectionReason;

    public Order() {}

    public Order(String productName, Double orderTotal, Long customerId) {
        this.productName = productName;
        this.orderTotal = orderTotal;
        this.customerId = customerId;
    }

    public void reject(RejectionReason rejectionReason) {
        this.state = OrderState.REJECTED;
        this.rejectionReason = rejectionReason;
    }

    public void approve() {
        this.state = OrderState.APPROVED;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public OrderState getState() {
        return state;
    }

    public RejectionReason getRejectionReason() {
        return rejectionReason;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }

    public void setOrderTotal(Double orderTotal) {
        this.orderTotal = orderTotal;
    }

    public Double getOrderTotal() {
        return this.orderTotal;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", orderTotal=" + orderTotal +
                ", customerId=" + customerId +
                ", state=" + state +
                ", rejectionReason=" + rejectionReason +
                '}';
    }

}

