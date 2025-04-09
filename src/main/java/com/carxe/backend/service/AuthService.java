package com.carxe.backend.service;

import com.carxe.backend.payload.request.RegisterRequest;

public interface AuthService {
    void registerUser(RegisterRequest registerRequest);
    boolean validateCredentials(String email, String password);
}
