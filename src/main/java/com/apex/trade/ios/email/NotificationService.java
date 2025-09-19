package com.apex.trade.ios.email;

import com.apex.trade.ios.registration.KycStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;

    public void sendKycStatusUpdateEmail(String toEmail, KycStatus status) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("KYC Status Update");
        message.setText("Your KYC status is now: " + status.name());
        mailSender.send(message);
    }
}
