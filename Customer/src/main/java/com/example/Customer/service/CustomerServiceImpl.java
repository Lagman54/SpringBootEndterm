package com.example.Customer.service;

import com.example.Customer.model.Customer;
import com.example.Customer.model.OrderDto;
import com.example.Customer.model.Payment;
import com.example.Customer.model.replies.CustomerInsufficientBalance;
import com.example.Customer.model.replies.CustomerNotFound;
import com.example.Customer.model.replies.CustomerPaymentResult;
import com.example.Customer.model.replies.CustomerPaymentSuccess;
import com.example.Customer.repo.CustomerRepository;
import com.example.Customer.repo.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final KafkaTemplate<String, CustomerPaymentResult> kafkaTemplate;

    @Value("${app.kafka.payment-result-topic}")
    private String paymentResultTopic;

    public CustomerServiceImpl(
            PaymentRepository paymentRepository,
            CustomerRepository customerRepository,
            KafkaTemplate<String, CustomerPaymentResult> kafkaTemplate
    ) {
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }


    @Override
    public void processPayment(OrderDto order) {
        Long customerId = order.customerId();
        Long orderId = order.orderId();
        Double orderTotal = order.orderTotal();

        try {
            Customer customer = customerRepository.findById(customerId)
                    .orElse(null);

            if (customer == null) {
                kafkaTemplate.send(paymentResultTopic,
                        new CustomerNotFound(customerId, orderId));
                return;
            }

            Long balance = customer.getBalance();
            if (balance < orderTotal) {
                kafkaTemplate.send(paymentResultTopic,
                        new CustomerInsufficientBalance(orderId, customerId, orderTotal, balance));
                return;
            }

            // Deduct balance and save
            customer.setBalance(balance - orderTotal.longValue());
            customerRepository.save(customer);

            // Save payment record
            Payment payment = new Payment(orderId, orderTotal, "PAID", customer);
            paymentRepository.save(payment);

            kafkaTemplate.send(paymentResultTopic,
                    new CustomerPaymentSuccess(orderId, customerId));

        } catch (Exception e) {
            e.printStackTrace();
            // Optional: add retry, dead-letter queue, or failure report
        }
    }
}
