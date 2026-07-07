package com.pilgrimtravel.api.email;

import com.pilgrimtravel.api.email.dto.EmailRequest;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class SendGridEmailService implements EmailService{
    private final SendGrid sendGrid;

    public SendGridEmailService(SendGrid sendGrid) {
        this.sendGrid = sendGrid;
    }

    @Override
    public boolean sendEmail(EmailRequest emailRequest) {
        Email from = new Email("your-verified-sendgrid-sender@example.com");
        Email to = new Email(emailRequest.getTo());
        Content content = new Content("text/plain", emailRequest.getBody());
        Mail mail = new Mail(from, emailRequest.getSubject(), to, content);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);
            return response.getStatusCode() >= 200 && response.getStatusCode() < 300;
        } catch (IOException ex) {
            return false;
        }
    }
}
