package com.example.Customer.model;

public record CustomerRegistrationDto (String username,
                                      String password,
                                      String name,       // optional; you’ll default to “John” if null
                                      Long balance ){
}
