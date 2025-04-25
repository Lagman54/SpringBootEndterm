package com.example.Customer.controller;

import com.example.Customer.model.AuthRequest;
import com.example.Customer.model.Customer;
import com.example.Customer.model.CustomerRegistrationDto;
import com.example.Customer.security.JwtTokenProvider;
import com.example.Customer.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final CustomerService customerService;
    private final JwtTokenProvider  jwtProvider;

    public AuthController(CustomerService cs, JwtTokenProvider jp) {
        this.customerService = cs;
        this.jwtProvider     = jp;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String,Object>> register(
            @RequestBody CustomerRegistrationDto dto) {
        Customer toSave = new Customer();
        toSave.setUsername(dto.username());
        toSave.setPassword(dto.password());
        toSave.setName(dto.name());
        toSave.setBalance(dto.balance());
        Customer saved = customerService.createCustomer(toSave);

        return ResponseEntity.ok(Map.of(
                "id",       saved.getId(),
                "username", saved.getUsername(),
                "name",     saved.getName(),
                "balance",  saved.getBalance()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(
            @RequestBody AuthRequest req) {
        Customer c = customerService.findByUsername(req.username());
        if (!customerService.checkPassword(c, req.password())) {
            return ResponseEntity.status(401).build();
        }
        String token = jwtProvider.createToken(c.getUsername(), c.getId().toString());
        return ResponseEntity.ok(Map.of(
                "username", c.getUsername(),
                "token",    token
        ));
    }
}
