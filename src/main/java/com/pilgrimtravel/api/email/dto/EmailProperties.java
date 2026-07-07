package com.pilgrimtravel.api.email.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "sendgrid.email")
public class EmailProperties {

    @NotBlank(message = "The SendGrid sender email must be configured.")
    @Email(message = "Invalid sender email format.")
    private String fromAddress;
}