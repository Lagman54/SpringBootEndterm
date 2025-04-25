package com.example.apigateway.client;

import com.example.apigateway.model.CustomerDto;
import com.example.apigateway.model.request.CustomerRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "customer-service", url = "http://localhost:8082")
public interface CustomerClient {
    @PostMapping("/api/customers")
    CustomerDto createCustomer(@RequestBody CustomerRequest request);
}
