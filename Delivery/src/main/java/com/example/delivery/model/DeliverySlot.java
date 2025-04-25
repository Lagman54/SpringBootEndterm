package com.example.delivery.model;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "delivery_slots")
public class DeliverySlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time")
    private LocalTime startTime;


    @Column(name = "end_time")
    private LocalTime endTime;

    private int quantity;

    @Column(name = "capacity")
    private int maxQuantity;

    public DeliverySlot() {}

    public Long getId() {
        return id;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

}
