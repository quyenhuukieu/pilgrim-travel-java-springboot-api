package com.pilgrimtravel.api.auth;

import com.pilgrimtravel.api.auth.dto.AuthResponse;
import com.pilgrimtravel.api.auth.dto.LoginRequest;
import com.pilgrimtravel.api.auth.dto.RegisterRequest;
import com.pilgrimtravel.api.auth.dto.UpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    // Handles HTTP login and registration requests

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PutMapping("/user")
    public ResponseEntity<AuthResponse> update(
            Authentication authentication,
            @RequestBody @Valid UpdateRequest request
            ) {
        String email = authentication.getName();
        return ResponseEntity.ok(userService.update(email, request));
    }
}
