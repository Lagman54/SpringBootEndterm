package com.example.apigateway.controller;

import com.example.apigateway.client.OrderClient;
import com.example.apigateway.model.OrderDto;
import com.example.apigateway.model.request.OrderRequest;
import com.example.apigateway.model.response.OrderResponse;
import com.example.apigateway.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);


    private final OrderClient orderClient;
    private final JwtService jwtService;

    public OrderController(OrderClient orderClient, JwtService jwtService) {
        this.orderClient = orderClient;
        this.jwtService = jwtService;
    }

    @PostMapping("/orders")
    public OrderResponse createOrder(@RequestBody OrderRequest request, HttpServletRequest httpRequest) {
        log.info("request to order {}", request);
        String token = extractTokenFromRequest(httpRequest);

        if (token == null) {
            throw new NullPointerException("JWT token is missing");
        }

        Long customerId = jwtService.extractCustomerId(token);

        OrderDto orderDto = new OrderDto(
                request.getProductName(),
                request.getOrderTotal(),
                customerId
        );

        return orderClient.createOrder(orderDto);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
