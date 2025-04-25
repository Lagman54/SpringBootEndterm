package com.example.Customer.controller;

import com.example.Customer.model.Customer;
import com.example.Customer.model.ForwardOrderRequest;
import com.example.Customer.model.Payment;
import com.example.Customer.repository.PaymentRepository;
import com.example.Customer.security.JwtTokenProvider;
import com.example.Customer.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private PaymentRepository paymentRepository;

    private final RestTemplate restTemplate;
    private final JwtTokenProvider jwtProvider;


    @Autowired
    private CustomerService customerService;

    @Autowired
    public CustomerController(RestTemplate restTemplate, JwtTokenProvider jwtProvider) {
        this.restTemplate = restTemplate;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/create")
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerService.createCustomer(customer);
    }

    @GetMapping
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }



    @PutMapping("/{id}")
    public Customer setNameAndBalance(
            @PathVariable Long id,
            @RequestBody Customer dto
    ) {
        return customerService.updateCustomer(
                id,
                dto.getName(),
                dto.getBalance()
        );
    }

    @PostMapping("/orders")
    public ResponseEntity<?> placeOrder(@RequestBody ForwardOrderRequest reqDto,
                                        HttpServletRequest servletReq) {
        String token = jwtProvider.resolveToken(servletReq);
        if (token == null || !jwtProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long customerId = Long.valueOf(jwtProvider.getCustomerId(token));

        Map<String,Object> payload = new HashMap<>();
        payload.put("productName", reqDto.productName());
        payload.put("orderTotal",  reqDto.orderTotal());
        payload.put("customerId",  customerId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String,Object>> httpEntity =
                new HttpEntity<>(payload, headers);

        ResponseEntity<String> orderResp = restTemplate.postForEntity(
                "http://localhost:8080/api/orders",
                httpEntity,
                String.class
        );

        return ResponseEntity
                .status(orderResp.getStatusCode())
                .body(orderResp.getBody());
    }
}
