package com.apex.trade.ios.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendVerificationEmail() {
        String to = "user@example.com";
        String token = "12345";

        emailService.sendVerificationEmail(to, token);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertNotNull(message.getTo());
        assertEquals(to, message.getTo()[0]);
        assertEquals("Verify your email", message.getSubject());
        assertNotNull(message.getText());
        assertTrue(message.getText().contains(token));
        assertTrue(message.getText().contains("http://localhost:8080/api/investors/verify-email?token="));
    }

    @Test
    public void testSendOtpEmail() {
        String toEmail = "otpuser@example.com";
        String otp = "67890";

        emailService.sendOtpEmail(toEmail, otp);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertNotNull(message.getTo());
        assertEquals(toEmail, message.getTo()[0]);
        assertEquals("Your OTP Code", message.getSubject());
        assertNotNull(message.getText());
        assertTrue(message.getText().contains(otp));
        assertTrue(message.getText().contains("expires in 5 minutes"));
    }

    @Test
    public void testSendPasswordResetEmail() {
        String email = "resetuser@example.com";
        String token = "reset-token";

        emailService.sendPasswordResetEmail(email, token);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertNotNull(message.getTo());
        assertEquals(email, message.getTo()[0]);
        assertEquals("Password Reset Request", message.getSubject());
        assertNotNull(message.getText());
        assertTrue(message.getText().contains(token));
        assertTrue(message.getText().contains("https://yourfrontend.com/reset-password?token="));
        assertTrue(message.getText().contains("expire in 30 minutes"));
    }
}
