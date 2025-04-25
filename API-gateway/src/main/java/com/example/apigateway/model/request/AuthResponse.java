package com.example.apigateway.model.request;

public class AuthResponse {
    private final String token;
    public AuthResponse(String token) {
        this.token = token;
    }
    public String getToken() {
        return token;
    }
}
