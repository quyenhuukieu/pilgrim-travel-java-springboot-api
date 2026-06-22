package com.pilgrimtravel.api.itinerary;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.mockito.Mockito;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(ItineraryController.class) // Focuses ONLY on your controller web layer
class ItineraryControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private ItineraryService itineraryService;

    @Autowired
    private ObjectMapper objectMapper;

    // =========================================================================
    // 1. GET BY ID OPERATION (Verifying Single Object Retrieval)
    // =========================================================================
    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void getItineraryById_returnsOkStatusAndSingleObject() throws Exception {
        // Arrange: Prepare expected domain entity mock data matching target ID 123L
        Itinerary expectedItinerary = new Itinerary(123L, 456L, "Fatima", LocalDate.of(2026, 10, 1), false);

        // Stub the exact findById service call to bypass real database extraction
        Mockito.when(itineraryService.findById(123L)).thenReturn(expectedItinerary);

        // Act & Assert using modern Spring Boot 4.1 Fluent BodyJson Mapping
        assertThat(this.mockMvc.get().uri("/api/itineraries/123"))
                .hasStatusOk() // Verifies HTTP 200 OK
                .bodyJson() // Evaluates response payload tree via Jackson 3
                .convertTo(Itinerary.class) // Maps the singular JSON object into our typed class
                .usingRecursiveComparison() // Inspects fields recursively by value
                .isEqualTo(expectedItinerary); // Direct structural object validation!
    }

    // =========================================================================
    // 2. GET BY USER ID OPERATION (Verifying List of Object Retrieval)
    // =========================================================================
    @Test
    @WithMockUser(username = "admin", roles = {"USER"}) // Generates a valid test security token
    void getUserItineraries_returnsList() throws Exception {
        // Arrange
        Itinerary it1 = new Itinerary(1L, 123L, "Fatima", LocalDate.of(2026, 10, 1), false);
        Itinerary it2 = new Itinerary(2L, 123L, "Rome", LocalDate.of(2026, 10, 5), true);

        List<Itinerary> sample = List.of(it1, it2);

        Mockito.when(itineraryService.findAllByUser(123L)).thenReturn(sample);

        // Act & Assert using Spring Boot 4.1 Fluent BodyJson Mapping
        assertThat(this.mockMvc.get().uri("/api/itineraries/user/123"))
                .hasStatusOk()
                .bodyJson() // 1. Swaps from raw text body to the dedicated Jackson JSON tree assertion handler
                .convertTo(InstanceOfAssertFactories.list(Itinerary.class)) // 2. Maps the array payload into a clean List<Itinerary>
                .usingRecursiveComparison() // 3. Recursively walks through collection elements element-by-element
                .ignoringFields("id")
                .isEqualTo(sample);
    }

    // =========================================================================
    // 3. POST OPERATION (With @ResponseStatus(HttpStatus.CREATED) Assertion)
    // =========================================================================
    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void createItinerary_returnsCreatedStatusAndObject() throws Exception {
        // Arrange incoming request body and expected saved database output
        Itinerary inputItinerary = new Itinerary(null, 123L, "Lourdes", LocalDate.of(2026, 11, 1), false);
        Itinerary savedItinerary = new Itinerary(99L, 123L, "Lourdes", LocalDate.of(2026, 11, 1), false);

        Mockito.when(itineraryService.create(any(Itinerary.class))).thenReturn(savedItinerary);

        // Act & Assert
        assertThat(this.mockMvc.post()
                .uri("/api/itineraries")
                .with(csrf()) // CRITICAL: Spring Security requires CSRF tokens on state-changing requests
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputItinerary)))
                .hasStatus(HttpStatus.CREATED) // Binds natively to HttpStatus.CREATED (201) assertion
                .bodyJson()
                .convertTo(Itinerary.class)
                .usingRecursiveComparison()
                .ignoringFields("id") // The saved entity gets an assigned database ID
                .isEqualTo(savedItinerary);
    }

    // =========================================================================
    // 4. PUT OPERATION
    // =========================================================================
    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void updateItinerary_returnsOkStatusAndUpdatedObject() throws Exception {
        // Arrange updating itinerary with ID 99L
        Itinerary updatedDetails = new Itinerary(99L, 123L, "Updated Rome", LocalDate.of(2026, 10, 6), true);

        Mockito.when(itineraryService.update(eq(99L), any(Itinerary.class))).thenReturn(updatedDetails);

        // Act & Assert
        assertThat(this.mockMvc.put()
                .uri("/api/itineraries/99")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDetails)))
                .hasStatusOk() // Verifies implicit HTTP 200 status
                .bodyJson()
                .convertTo(Itinerary.class)
                .usingRecursiveComparison()
                .isEqualTo(updatedDetails);
    }

    // =========================================================================
    // 5. DELETE OPERATION (With @ResponseStatus(HttpStatus.NO_CONTENT) Assertion)
    // =========================================================================
    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void deleteItinerary_returnsNoContentStatus() throws Exception {
        // Act & Assert
        assertThat(this.mockMvc.delete()
                .uri("/api/itineraries/99")
                .with(csrf()))
                .hasStatus(HttpStatus.NO_CONTENT); // Binds to HttpStatus.NO_CONTENT (204) assertion

        // Verify that the downstream service layer deletion rule was exactly executed once
        Mockito.verify(itineraryService, Mockito.times(1)).delete(99L);
    }
}
