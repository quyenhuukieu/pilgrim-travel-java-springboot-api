package com.pilgrimtravel.api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    // DTO for user credentials
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
