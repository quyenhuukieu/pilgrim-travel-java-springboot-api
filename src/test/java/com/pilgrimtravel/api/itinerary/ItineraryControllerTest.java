package com.pilgrimtravel.api.itinerary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pilgrimtravel.api.auth.User;
import com.pilgrimtravel.api.security.JwtAuthFilter;
import com.pilgrimtravel.api.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItineraryController.class)
class ItineraryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItineraryService itineraryService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        // 🌟 Force Jackson to write dates as ISO strings instead of numeric arrays
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public FilterRegistrationBean<JwtAuthFilter> registration(JwtAuthFilter filter) {
            FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>(filter);
            // This flag explicitly tells the test servlet wrapper NOT to execute this filter
            registration.setEnabled(false);
            return registration;
        }
    }

    private UsernamePasswordAuthenticationToken mockAuthentication() {

        User user = new User(
                1L,
                "test@example.com",
                "encoded-password-placeholder",
                false
        );

        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities()
        );
    }

    // =========================
    // GET BY ID
    // =========================
    @Test
    void getItineraryById_returnsOk() throws Exception {
        // 1. Setup sample payload
        Itinerary itinerary = new Itinerary(
                123L, 1L, "Rome", LocalDate.of(2026, 10, 1), false
        );

        String email = "test@example.com";

        // 2. Mock the controller's internal business logic
        Mockito.when(itineraryService.findById(email,123L))
                .thenReturn(itinerary);

        mockMvc.perform(get("/api/itineraries/123")
                .principal(mockAuthentication()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itinerary)));
    }

    // =========================
    // GET USER ITINERARIES
    // =========================
    @Test
    void getUserItineraries_returnsList() throws Exception {
        // 1. Setup sample payload
        List<Itinerary> sample = List.of(
                new Itinerary(1L, 1L, "Rome", LocalDate.now(), false),
                new Itinerary(2L, 1L, "Paris", LocalDate.now(), true)
        );

        String email = "test@example.com";

        // 2. Mock the controller's internal business logic
        Mockito.when(itineraryService.findAllByUser(email)).thenReturn(sample);

        // 3. Execute MockMvc by forcing the mock security token into the request context
        mockMvc.perform(get("/api/itineraries/user")
                .principal(mockAuthentication()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(sample)));
    }

    // =========================
    // CREATE
    // =========================
    @Test
    void createItinerary_returnsCreated() throws Exception {
        // 1. Setup sample payload
        Itinerary input = new Itinerary(
                null, 1L, "Lourdes", LocalDate.of(2026, 11, 1), false
        );

        Itinerary saved = new Itinerary(
                99L, 1L, "Lourdes", LocalDate.of(2026, 11, 1), false
        );

        // 2. Mock the controller's internal business logic
        Mockito.when(itineraryService.create(eq("test@example.com"), any(Itinerary.class)))
                .thenReturn(saved);

        mockMvc.perform(post("/api/itineraries")
                        .with(csrf())
                        .principal(mockAuthentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(saved)));
    }

    // =========================
    // UPDATE
    // =========================
    @Test
    void updateItinerary_returnsOk() throws Exception {
        // 1. Setup sample payload
        Itinerary updated = new Itinerary(
                99L, 1L, "Updated Rome", LocalDate.of(2026, 10, 6), true
        );

        // 2. Mock the controller's internal business logic
        Mockito.when(itineraryService.update(eq("test@example.com"), eq(99L), any(Itinerary.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/api/itineraries/99")
                        .with(csrf())
                        .principal(mockAuthentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updated)));
    }

    // =========================
    // DELETE
    // =========================
    @Test
    void deleteItinerary_returnsNoContent() throws Exception {
        String email = "test@example.com";
        Mockito.doNothing().when(itineraryService).delete(email, 99L);

        mockMvc.perform(delete("/api/itineraries/99")
                        .with(csrf())
                .principal(mockAuthentication()))
                .andExpect(status().isNoContent());

        Mockito.verify(itineraryService).delete(email,99L);
    }
}