package internship.paymentSystem.backend.services.interfaces;

import org.springframework.mail.SimpleMailMessage;

public interface IEmailService {

    void sendEmail(String toEmail, String subject, String message);
}
