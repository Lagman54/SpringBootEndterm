package com.example.apigateway.client;

import com.example.apigateway.model.OrderDto;
import com.example.apigateway.model.response.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "order-service", url = "http://localhost:8080")
public interface OrderClient {
    @PostMapping("/api/orders")
    OrderResponse createOrder(@RequestBody OrderDto request);
}