package com.example.Customer.service;


import com.example.Customer.model.Payment;
import com.example.Customer.repo.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public void processPaymentEvent(String message) {
        // Example: parse the message "ORDER_CREATED: <id>"
        // Very simplified string parsing logic:
        if (message.startsWith("ORDER_CREATED:")) {
            String orderIdStr = message.replace("ORDER_CREATED:", "").trim();
            Long orderId = Long.parseLong(orderIdStr);

            // Just create a Payment in "PENDING" status for that Order
            Payment payment = new Payment(orderId, 100.0, "PENDING");
            paymentRepository.save(payment);

            System.out.println("Created Payment record for orderId=" + orderId);
        }
    }
}