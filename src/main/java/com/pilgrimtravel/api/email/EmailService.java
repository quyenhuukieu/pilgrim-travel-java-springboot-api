package com.pilgrimtravel.api.email;

import com.pilgrimtravel.api.email.dto.EmailRequest;

public interface EmailService {
    boolean sendEmail(EmailRequest emailRequest);
}
