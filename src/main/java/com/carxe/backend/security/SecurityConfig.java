package com.carxe.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(JwtTokenProvider tokenProvider, UserDetailsServiceImpl userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions().sameOrigin())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/cars").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/cars/featured").permitAll()
                .requestMatchers("/api/cars/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/bookings").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.POST, "/api/bookings").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/api/bookings/**").authenticated()
                
                // Allow ADMIN role to cancel bookings
                .requestMatchers(HttpMethod.POST, "/api/bookings/**/cancel").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .headers(headers -> headers
                .contentTypeOptions().disable()
            )
            .addFilterBefore(new JwtAuthenticationFilter(tokenProvider, userDetailsService), 
                UsernamePasswordAuthenticationFilter.class);

        System.out.println("Security config initialized:");
        System.out.println("- POST /api/bookings requires ADMIN or USER role");
        System.out.println("- Other booking endpoints require authentication");
        
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Chỉ định rõ các origin được phép để đảm bảo bảo mật
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:5173"
        ));
        
        // Cho phép tất cả các HTTP methods quan trọng
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"
        ));
        
        // Cho phép tất cả các headers bao gồm cả Authorization
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", "Cache-Control", "Content-Type"
        ));
        
        // Xuất các headers quan trọng
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Requested-With"
        ));
        
        // Bật credentials - cần thiết cho JWT
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        System.out.println("==== CORS Configuration ====");
        System.out.println("Allowed Origins: " + configuration.getAllowedOrigins());
        System.out.println("Allowed Methods: " + configuration.getAllowedMethods());
        System.out.println("Allowed Headers: " + configuration.getAllowedHeaders());
        
        return source;
    }
}
