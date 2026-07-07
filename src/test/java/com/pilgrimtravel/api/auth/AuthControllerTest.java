package com.pilgrimtravel.api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pilgrimtravel.api.auth.dto.AuthResponse;
import com.pilgrimtravel.api.auth.dto.LoginRequest;
import com.pilgrimtravel.api.auth.dto.RegisterRequest;
import com.pilgrimtravel.api.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {

        RegisterRequest request =
                new RegisterRequest(
                        "test@example.com",
                        "password123",
                        false
                );


        AuthResponse response =
                new AuthResponse("jwt-token");


        when(userService.register(any(RegisterRequest.class)))
                .thenReturn(response);


        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token")
                        .value("jwt-token"));


        verify(userService)
                .register(any(RegisterRequest.class));
    }



    @Test
    void shouldLoginUserSuccessfully() throws Exception {

        LoginRequest request =
                new LoginRequest(
                        "test@example.com",
                        "password123"
                );


        AuthResponse response =
                new AuthResponse("jwt-token");


        when(userService.login(any(LoginRequest.class)))
                .thenReturn(response);


        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token")
                        .value("jwt-token"));


        verify(userService)
                .login(any(LoginRequest.class));
    }



    @Test
    void shouldRejectRegistrationWhenEmailIsBlank() throws Exception {

        RegisterRequest request =
                new RegisterRequest(
                        "",
                        "password123",
                        false
                );


        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }



    @Test
    void shouldRejectRegistrationWhenPasswordIsBlank() throws Exception {

        RegisterRequest request =
                new RegisterRequest(
                        "test@example.com",
                        "",
                        false
                );


        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }



    @Test
    void shouldRejectLoginWhenCredentialsAreBlank() throws Exception {

        LoginRequest request =
                new LoginRequest(
                        "",
                        ""
                );


        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }
}