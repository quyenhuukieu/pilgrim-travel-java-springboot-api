package com.pilgrimtravel.api.auth;

import com.pilgrimtravel.api.auth.dto.AuthResponse;
import com.pilgrimtravel.api.auth.dto.LoginRequest;
import com.pilgrimtravel.api.auth.dto.RegisterRequest;
import com.pilgrimtravel.api.auth.dto.UpdateRequest;
import com.pilgrimtravel.api.email.EmailService;
import com.pilgrimtravel.api.email.dto.EmailRequest;
import com.pilgrimtravel.api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger log = LogManager.getLogger(UserService.class);
    // Main user business logic

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthResponse register(RegisterRequest request) {
        if (request.getEmail() != null &&
                userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Resource already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        // Always hash passwords
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        EmailRequest emailRequest = EmailRequest.builder()
                .to(request.getEmail())
                .subject("Please verify your email")
                .body("Thanks for signing up! To verify your email")
                .build();

        try {
            emailService.sendEmail(emailRequest);
        } catch (Exception ex) {
            log.info(ex);
            throw new RuntimeException("Failed to send email");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    public AuthResponse update(String email, UpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(user);

        return new AuthResponse(jwtService.generateToken(user));
    }
}
