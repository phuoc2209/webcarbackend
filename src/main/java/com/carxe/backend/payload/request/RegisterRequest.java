package com.carxe.backend.payload.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    @NotBlank
    private String fullName;

    @NotBlank
    @Pattern(regexp = "\\d{10}")
    private String phone;
}
