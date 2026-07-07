package com.pilgrimtravel.api.email;

import com.pilgrimtravel.api.email.dto.EmailRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest request) {
        boolean isSent = emailService.sendEmail(request);
        if (isSent) {
            return ResponseEntity.ok("Email processed successfully.");
        } else {
            return ResponseEntity.internalServerError().body("Failed to send email via SendGrid.");
        }
    }
}
