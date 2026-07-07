package com.pilgrimtravel.api.email;

import com.pilgrimtravel.api.email.config.SendGridConfig;
import com.pilgrimtravel.api.email.dto.EmailRequest;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // New import for Spring Boot 4+

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// We isolate this slice test by importing only the components needed for the email feature
@SpringBootTest(classes = {SendGridEmailService.class})
@Import(SendGridConfig.class)
public class SendGridEmailServiceTest {

    @Autowired
    private SendGridEmailService emailService;

    // Replaces the legacy @MockBean in Spring Boot 4.x
    @MockitoBean
    private SendGrid sendGrid;

    @Test
    void shouldReturnTrueWhenSendGridSucceeds() throws IOException {
        // Arrange
        EmailRequest requestDto = EmailRequest.builder()
                .to("user@example.com")
                .subject("Spring Boot 4.1 Test")
                .body("Testing JDK 25 features")
                .build();

        Response mockResponse = new Response();
        mockResponse.setStatusCode(202);
        mockResponse.setBody("Accepted");

        when(sendGrid.api(any(Request.class))).thenReturn(mockResponse);

        // Act
        boolean result = emailService.sendEmail(requestDto);

        // Assert
        assertTrue(result);

        // Verify call structure using ArgumentCaptor
        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        verify(sendGrid, times(1)).api(requestCaptor.capture());
    }

    @Test
    void shouldReturnFalseWhenSendGridFails() throws IOException {
        // Arrange
        EmailRequest requestDto = EmailRequest.builder()
                .to("user@example.com")
                .subject("Fail Test")
                .body("Testing Error Paths")
                .build();

        Response mockResponse = new Response();
        mockResponse.setStatusCode(401);

        when(sendGrid.api(any(Request.class))).thenReturn(mockResponse);

        // Act
        boolean result = emailService.sendEmail(requestDto);

        // Assert
        assertFalse(result);
    }
}
