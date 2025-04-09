package com.carxe.backend.service;

import com.carxe.backend.model.User;
import com.carxe.backend.payload.request.RegisterRequest;
import com.carxe.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, 
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setPhone(registerRequest.getPhone());
        user.setRoles(Collections.singleton("USER"));

        userRepository.save(user);
    }

    @Override
    public boolean validateCredentials(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        System.out.println("Input password: " + password);
        System.out.println("Stored password: " + user.getPassword());
        System.out.println("Matches: " + passwordEncoder.matches(password, user.getPassword()));
        
        return passwordEncoder.matches(password, user.getPassword());
    }
}
