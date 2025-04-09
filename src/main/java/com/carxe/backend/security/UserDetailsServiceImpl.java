package com.carxe.backend.security;

import com.carxe.backend.model.User;
import com.carxe.backend.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Loading user by username: " + username);
        
        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> {
                System.err.println("User not found with email: " + username);
                return new UsernameNotFoundException("User not found with email: " + username);
            });

        System.out.println("Found user: " + user.getEmail());
        System.out.println("User roles: " + user.getRoles());

        Collection<GrantedAuthority> authorities = user.getRoles().stream()
            .map(role -> {
                String roleName = "ROLE_" + role;
                System.out.println("Granting authority: " + roleName);
                return new SimpleGrantedAuthority(roleName);
            })
            .collect(Collectors.toList());

        return org.springframework.security.core.userdetails.User
            .withUsername(user.getEmail())
            .password(user.getPassword())
            .authorities(authorities)
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build();
    }
}
