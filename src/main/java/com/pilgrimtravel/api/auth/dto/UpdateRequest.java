package com.pilgrimtravel.api.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRequest {
    // DTO for user account creation data
    @Email(message = "Please provide a valid email address")
    private String email;

    @Size(min = 6, max = 100)
    private String password;

    // Forces validation failure if a client passes "true"
    @AssertFalse(message = "New registrations cannot be pre-verified")
    private boolean isVerified = false;
}
