package com.example.apigateway.controller;

import com.example.apigateway.model.OrderDto;
import com.example.apigateway.model.request.OrderRequest;
import com.example.apigateway.model.response.OrderResponse;
import com.example.apigateway.service.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.parameters.RequestBody;  // <-- Swagger RequestBody

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Tag(name = "Orders", description = "Endpoints for creating and retrieving orders")
@RestController
@RequestMapping("/api")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final JwtService jwtService;
    private final RestTemplate restTemplate;

    /**
     * Base URL of the Order service; override in application.properties if needed
     */
    @Value("${order.service.base-url:http://localhost:8080}")
    private String orderServiceBaseUrl;

    public OrderController(JwtService jwtService) {
        this.jwtService = jwtService;
        this.restTemplate = new RestTemplate();
    }

    @Operation(
            summary = "Create a new order",
            description = "Places an order on behalf of the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order created successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")
    })
    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody(
                    description = "Order details payload",
                    required = true
            )
            @org.springframework.web.bind.annotation.RequestBody OrderRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("Incoming createOrder request: {}", request);

        String token = extractToken(httpRequest);
        Long customerId = jwtService.extractCustomerId(token);

        OrderDto dto = new OrderDto(
                request.getProductName(),
                request.getOrderTotal(),
                customerId
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<OrderDto> entity = new HttpEntity<>(dto, headers);

        ResponseEntity<OrderResponse> response = restTemplate
                .postForEntity(orderServiceBaseUrl + "/api/orders", entity, OrderResponse.class);

        return ResponseEntity
                .status(response.getStatusCode())
                .body(response.getBody());
    }

    @Operation(
            summary = "Get all orders",
            description = "Retrieves all orders from the Order service"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")
    })
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders(HttpServletRequest httpRequest) {
        log.info("Incoming getAllOrders request");

        String token = extractToken(httpRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ParameterizedTypeReference<List<OrderResponse>> typeRef =
                new ParameterizedTypeReference<>() {};

        ResponseEntity<List<OrderResponse>> response = restTemplate.exchange(
                orderServiceBaseUrl + "/api/orders",
                HttpMethod.GET,
                entity,
                typeRef
        );

        return ResponseEntity
                .status(response.getStatusCode())
                .body(response.getBody());
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException("JWT token is missing or malformed");
        }
        return header.substring(7);
    }
}
