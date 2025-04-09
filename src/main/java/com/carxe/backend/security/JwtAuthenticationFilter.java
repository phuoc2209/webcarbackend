package com.carxe.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, UserDetailsServiceImpl userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        System.out.println("Processing request to: " + request.getRequestURI());
        if (request.getRequestURI().equals("/api/cars/featured")) {
            System.out.println("Allowing featured cars endpoint without authentication");
            filterChain.doFilter(request, response);
            return;
        }
    System.out.println("=== Incoming Request ===");
    System.out.println("URI: " + request.getRequestURI());
    System.out.println("Method: " + request.getMethod());
    System.out.println("Headers: ");
    Collections.list(request.getHeaderNames()).forEach(headerName -> 
        System.out.println(headerName + ": " + request.getHeader(headerName)));
        try {
            String jwt = getJwtFromRequest(request);
            System.out.println("JWT Token: " + jwt);

            if (StringUtils.hasText(jwt)) {
                System.out.println("Validating token...");
                if (tokenProvider.validateToken(jwt)) {
                    String username = tokenProvider.getUsernameFromToken(jwt);
                    System.out.println("Extracted username: " + username);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    System.out.println("User authorities: " + userDetails.getAuthorities());
                
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
