package com.example.apigateway.controller;

import com.example.apigateway.client.CustomerClient;
import com.example.apigateway.model.CustomerDto;
import com.example.apigateway.model.User;
import com.example.apigateway.model.request.RegisterRequest;
import com.example.apigateway.model.request.LoginRequest;
import com.example.apigateway.model.request.CustomerRequest;
import com.example.apigateway.model.response.AuthResponse;
import com.example.apigateway.repository.UserRepository;
import com.example.apigateway.service.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.parameters.RequestBody;  // <-- Swagger RequestBody

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "Authentication", description = "Endpoints for registering and logging in users")
@RestController
@RequestMapping("/api")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final UserRepository userRepository;
    private final CustomerClient customerClient;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository,
                          CustomerClient customerClient,
                          JwtService jwtService) {
        this.userRepository = userRepository;
        this.customerClient = customerClient;
        this.jwtService = jwtService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Operation(summary = "Health check", description = "Returns OK if the gateway is running")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user, persists credentials, provisions a Customer, and returns a JWT"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registration successful"),
            @ApiResponse(responseCode = "400", description = "User with given email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody(                                      // <-- Swagger annotation
                    description = "User registration payload",
                    required = true
            )
            @org.springframework.web.bind.annotation.RequestBody  // <-- Spring annotation
            RegisterRequest request
    ) {
        log.info("received request {}", request.getEmail());
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.info("user already registered {}", request.getEmail());
            return ResponseEntity.badRequest().build();
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        log.info("sending request to customer {}", request.getEmail());
        CustomerDto customer = customerClient.createCustomer(
                new CustomerRequest(request.getName())
        );
        user.setCustomerId(customer.getId());

        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @Operation(
            summary = "Authenticate a user",
            description = "Validates credentials and returns a JWT"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid email or password")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody(
                    description = "User login payload",
                    required = true
            )
            @org.springframework.web.bind.annotation.RequestBody
            LoginRequest request
    ) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).build();
        }

        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
