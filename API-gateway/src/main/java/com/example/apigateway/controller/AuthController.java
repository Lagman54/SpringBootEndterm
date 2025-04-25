package com.example.apigateway.controller;

import com.example.apigateway.client.CustomerClient;
import com.example.apigateway.model.CustomerDto;
import com.example.apigateway.model.User;
import com.example.apigateway.model.request.AuthResponse;
import com.example.apigateway.model.request.CustomerRequest;
import com.example.apigateway.model.request.LoginRequest;
import com.example.apigateway.model.request.RegisterRequest;
import com.example.apigateway.repository.UserRepository;
import com.example.apigateway.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
class AuthController {

    private final UserRepository userRepository;
    private final CustomerClient customerClient;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    AuthController(UserRepository userRepository, CustomerClient customerClient, JwtService jwtService) {
        this.userRepository = userRepository;
        this.customerClient = customerClient;
        this.jwtService = jwtService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        User user = new User();
        user.setEmail(request.getEmail());

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        CustomerDto customer = customerClient.createCustomer(new CustomerRequest(request.getName()));
        user.setCustomerId(customer.getId());

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) return ResponseEntity.status(401).build();

        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).build();
        }

        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
