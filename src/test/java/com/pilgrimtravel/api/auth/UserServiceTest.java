package com.pilgrimtravel.api.auth;

import com.pilgrimtravel.api.auth.dto.AuthResponse;
import com.pilgrimtravel.api.auth.dto.LoginRequest;
import com.pilgrimtravel.api.auth.dto.RegisterRequest;
import com.pilgrimtravel.api.email.EmailService;
import com.pilgrimtravel.api.email.dto.EmailRequest;
import com.pilgrimtravel.api.security.JwtService;

import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    @Mock
    private EmailService emailService;

    public UserServiceTest() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void shouldRegisterUserSuccessfully() {

        RegisterRequest request =
                new RegisterRequest("test@example.com",
                        "password123",
                        false);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("password123"))
                .thenReturn("hashed-password");

        when(jwtService.generateToken(any(User.class)))
                .thenReturn("jwt-token");

        when(emailService.sendEmail(any(EmailRequest.class)))
                .thenReturn(true);

        AuthResponse response =
                userService.register(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());

        verify(userRepository).save(any(User.class));
        verify(emailService).sendEmail(any(EmailRequest.class));
    }


    @Test
    void shouldFailLoginWhenUserNotFound() {

        LoginRequest request =
                new LoginRequest("missing@example.com", "password123");

        when(userRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userService.login(request));
    }


    @Test
    void shouldLoginSuccessfully() {

        LoginRequest request =
                new LoginRequest("test@example.com", "password123");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("hashed-password");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(authenticationManager.authenticate(any()))
                .thenReturn(mock(Authentication.class));

        when(passwordEncoder.matches("password123", "hashed-password"))
                .thenReturn(true);

        when(jwtService.generateToken(user))
                .thenReturn("jwt-token");

        AuthResponse response =
                userService.login(request);

        assertEquals("jwt-token", response.getToken());
    }


    @Test
    void shouldFailLoginWhenPasswordIncorrect() {

        LoginRequest request =
                new LoginRequest("test@example.com", "wrong-password");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("hashed-password");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        when(passwordEncoder.matches("wrong-password", "hashed-password"))
                .thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> userService.login(request));
    }
}