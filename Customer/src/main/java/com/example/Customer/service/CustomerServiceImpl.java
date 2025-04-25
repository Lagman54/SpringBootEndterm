package com.example.Customer.service;

import com.example.Customer.model.Customer;
import com.example.Customer.model.OrderDto;
import com.example.Customer.model.OutboxEvent;
import com.example.Customer.model.Payment;
import com.example.Customer.model.replies.CustomerInsufficientBalance;
import com.example.Customer.model.replies.CustomerNotFound;
import com.example.Customer.model.replies.CustomerPaymentSuccess;
import com.example.Customer.repo.CustomerRepository;
import com.example.Customer.repository.OutboxRepository;
import com.example.Customer.repository.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger log = LogManager.getLogger(CustomerServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.payment-result-topic}")
    private String paymentResultTopic;

    public CustomerServiceImpl(
            PaymentRepository paymentRepository,
            CustomerRepository customerRepository,
            OutboxRepository outboxRepository,
            ObjectMapper objectMapper
    ) {
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public Customer createCustomer(Customer customer) {
        log.info("Created Customer {}", customer);
        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void processPayment(OrderDto order) {
        Long customerId = order.customerId();
        Long orderId = order.orderId();
        Double orderTotal = order.orderTotal();

        try {
            Customer customer = customerRepository.findById(customerId)
                    .orElse(null);

            if (customer == null) {
                log.info("Customer with id {} not found", customerId);
                outboxRepository.save(toOutbox(
                        new CustomerNotFound(customerId, orderId),
                        "customer.payment.not_found",
                        orderId
                ));
                return;
            }

            Long balance = customer.getBalance();
            if (balance < orderTotal) {
                log.info("Customer with id {} balance {} not enough", customerId, balance);
                outboxRepository.save(toOutbox(
                        new CustomerInsufficientBalance(orderId, customerId, orderTotal, balance),
                        "customer.payment.insufficient",
                        orderId
                ));
                return;
            }

            // Deduct balance and save
            customer.setBalance(balance - orderTotal.longValue());
            customerRepository.save(customer);

            // Save payment record
            Payment payment = new Payment(orderId, orderTotal, "PAID", customer);
            paymentRepository.save(payment);

            log.info("Customer with id {} payment success", customerId);

            outboxRepository.save(toOutbox(
                    new CustomerPaymentSuccess(orderId, customerId),
                    "customer.payment.success",
                    orderId
            ));

        } catch (Exception e) {
            log.error("Failed to process payment for order {}", orderId, e);
            // Optionally log an outbox failure event or track error metrics
        }
    }

    private OutboxEvent toOutbox(Object eventObject, String type, Long orderId) throws JsonProcessingException {
        OutboxEvent event = new OutboxEvent();
        event.setId(UUID.randomUUID());
        event.setAggregateType("customer");
        event.setAggregateId(orderId.toString());
        event.setType(type);
        event.setPayload(objectMapper.writeValueAsString(eventObject)); // 👈 JUST STRING
        return event;
    }

}
