package com.example.Customer.services;


import com.example.Customer.models.OrderEvent;
import com.example.Customer.models.Payment;
import com.example.Customer.repo.PaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        try {
            ObjectMapper mapper = new ObjectMapper();
            OrderEvent event = mapper.readValue(message, OrderEvent.class);

            Payment payment = new Payment(
                    event.getOrderId(),
                    event.getPrice().doubleValue(),
                    "PENDING"
            );

            paymentRepository.save(payment);
            System.out.println("✅ Saved payment from JSON: " + message);

        } catch (Exception e) {
            System.err.println("❌ Failed to parse Kafka message: " + message);
            e.printStackTrace();
        }
    }
}