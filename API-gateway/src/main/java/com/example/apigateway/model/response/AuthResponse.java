package com.example.apigateway.model.response;

public class AuthResponse {
    private final String token;
    public AuthResponse(String token) {
        this.token = token;
    }
    public String getToken() {
        return token;
    }
}
