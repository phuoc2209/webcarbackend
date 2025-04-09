package com.carxe.backend.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private List<String> roles;
    
    public JwtResponse(String token, List<String> roles) {
        this.token = token;
        this.roles = roles;
    }
}
